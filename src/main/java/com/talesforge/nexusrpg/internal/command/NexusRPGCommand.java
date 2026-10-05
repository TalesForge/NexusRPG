package com.talesforge.nexusrpg.internal.command;

import com.talesforge.nexusrpg.api.NexusRPGApi;
import com.talesforge.nexusrpg.api.NexusRPGRegistries;
import com.talesforge.nexusrpg.api.buff.BuffInstance;
import com.talesforge.nexusrpg.api.profile.RpgProfile;
import com.talesforge.nexusrpg.api.team.RpgTeam;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * /nexusrpg faction set|clear <target> [id]
 * /nexusrpg class add|remove <target> <id>
 * /nexusrpg team create <name> [founder] | join <name> <target> | leave <target> | disband <name>
 * /nexusrpg buff give <target> <id> <seconds>
 */
public final class NexusRPGCommand {
    private static final SimpleCommandExceptionType NOT_LIVING =
            new SimpleCommandExceptionType(Component.literal("Target is not a living entity"));
    private static final SimpleCommandExceptionType FAILED =
            new SimpleCommandExceptionType(Component.literal("Operation failed or was cancelled"));
    private static final SimpleCommandExceptionType NO_TEAM =
            new SimpleCommandExceptionType(Component.literal("No such team"));

    private static final SuggestionProvider<CommandSourceStack> FACTIONS = (c, b) -> SharedSuggestionProvider.suggestResource(
            c.getSource().registryAccess().registryOrThrow(NexusRPGRegistries.FACTION_KEY).keySet(), b);
    private static final SuggestionProvider<CommandSourceStack> CLASSES = (c, b) -> SharedSuggestionProvider.suggestResource(
            c.getSource().registryAccess().registryOrThrow(NexusRPGRegistries.CLASS_KEY).keySet(), b);
    private static final SuggestionProvider<CommandSourceStack> BUFFS = (c, b) ->
            SharedSuggestionProvider.suggestResource(NexusRPGRegistries.BUFF_TYPES.keySet(), b);

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("nexusrpg").requires(s -> s.hasPermission(2))
                .then(Commands.literal("faction")
                        .then(Commands.literal("set").then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("id", ResourceLocationArgument.id()).suggests(FACTIONS)
                                        .executes(c -> faction(c, ResourceLocationArgument.getId(c, "id"))))))
                        .then(Commands.literal("clear").then(Commands.argument("target", EntityArgument.entity())
                                .executes(c -> faction(c, null)))))
                .then(Commands.literal("class")
                        .then(Commands.literal("add").then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("id", ResourceLocationArgument.id()).suggests(CLASSES)
                                        .executes(c -> classChange(c, true)))))
                        .then(Commands.literal("remove").then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("id", ResourceLocationArgument.id()).suggests(CLASSES)
                                        .executes(c -> classChange(c, false))))))
                .then(Commands.literal("team")
                        .then(Commands.literal("create").then(Commands.argument("name", StringArgumentType.word())
                                .executes(c -> teamCreate(c, null))
                                .then(Commands.argument("founder", EntityArgument.entity())
                                        .executes(c -> teamCreate(c, living(c, "founder"))))))
                        .then(Commands.literal("join").then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("target", EntityArgument.entity()).executes(NexusRPGCommand::teamJoin))))
                        .then(Commands.literal("leave").then(Commands.argument("target", EntityArgument.entity())
                                .executes(NexusRPGCommand::teamLeave)))
                        .then(Commands.literal("disband").then(Commands.argument("name", StringArgumentType.word())
                                .executes(NexusRPGCommand::teamDisband))))
                .then(Commands.literal("info").then(Commands.argument("target", EntityArgument.entity())
                        .executes(NexusRPGCommand::info)))
                .then(Commands.literal("reset").then(Commands.argument("target", EntityArgument.entity())
                        .executes(NexusRPGCommand::reset)))
                .then(Commands.literal("buff").then(Commands.literal("give")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("id", ResourceLocationArgument.id()).suggests(BUFFS)
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                                .executes(NexusRPGCommand::buffGive))))))
        );
    }

    private static int faction(CommandContext<CommandSourceStack> c, @Nullable ResourceLocation id) throws CommandSyntaxException {
        LivingEntity t = living(c, "target");
        if (!NexusRPGApi.profiles().setFaction(t, id)) throw FAILED.create();
        ok(c, id == null ? "Faction cleared" : "Faction set to " + id);
        return 1;
    }

    private static int classChange(CommandContext<CommandSourceStack> c, boolean add) throws CommandSyntaxException {
        LivingEntity t = living(c, "target");
        ResourceLocation id = ResourceLocationArgument.getId(c, "id");
        boolean done = add ? NexusRPGApi.profiles().addClass(t, id) : NexusRPGApi.profiles().removeClass(t, id);
        if (!done) throw FAILED.create();
        ok(c, (add ? "Added class " : "Removed class ") + id);
        return 1;
    }

    private static int teamCreate(CommandContext<CommandSourceStack> c, @Nullable LivingEntity founder) throws CommandSyntaxException {
        String name = StringArgumentType.getString(c, "name");
        RpgTeam team = NexusRPGApi.teams().create(c.getSource().getServer(), name, founder).orElseThrow(FAILED::create);
        ok(c, "Created team " + team.name());
        return 1;
    }

    private static int teamJoin(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        RpgTeam team = NexusRPGApi.teams().findByName(c.getSource().getServer(), StringArgumentType.getString(c, "name"))
                .orElseThrow(NO_TEAM::create);
        if (!NexusRPGApi.teams().join(team.id(), living(c, "target"))) throw FAILED.create();
        ok(c, "Joined team " + team.name());
        return 1;
    }

    private static int teamLeave(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        if (!NexusRPGApi.teams().leave(living(c, "target"))) throw FAILED.create();
        ok(c, "Left team");
        return 1;
    }

    private static int teamDisband(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        var server = c.getSource().getServer();
        RpgTeam team = NexusRPGApi.teams().findByName(server, StringArgumentType.getString(c, "name")).orElseThrow(NO_TEAM::create);
        NexusRPGApi.teams().disband(server, team.id());
        ok(c, "Disbanded team " + team.name());
        return 1;
    }

    private static int buffGive(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        LivingEntity t = living(c, "target");
        ResourceLocation id = ResourceLocationArgument.getId(c, "id");
        int ticks = IntegerArgumentType.getInteger(c, "seconds") * 20;
        if (!NexusRPGApi.buffs().apply(t, BuffInstance.of(id, 1, ticks))) throw FAILED.create();
        ok(c, "Gave buff " + id);
        return 1;
    }

    private static int info(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        LivingEntity t = living(c, "target");
        RpgProfile p = NexusRPGApi.profiles().get(t);   // effective values (defaults applied)
        boolean custom = NexusRPGApi.profiles().isCustomized(t);
        String text = "Faction: " + p.faction().map(ResourceLocation::toString).orElse("none")
                + "\nClasses: " + p.classes()
                + "\nTeam: " + p.teamId().map(UUID::toString).orElse("none")
                + "\nBuffs: " + p.buffs().size()
                + "\nSource: " + (custom ? "custom (stored on entity)" : "defaults of entity type");
        c.getSource().sendSuccess(() -> Component.literal(text), false);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        NexusRPGApi.profiles().resetToDefaults(living(c, "target"));
        ok(c, "Faction and classes reset to defaults");
        return 1;
    }

    private static LivingEntity living(CommandContext<CommandSourceStack> c, String arg) throws CommandSyntaxException {
        Entity e = EntityArgument.getEntity(c, arg);
        if (e instanceof LivingEntity le) return le;
        throw NOT_LIVING.create();
    }

    private static void ok(CommandContext<CommandSourceStack> c, String msg) {
        c.getSource().sendSuccess(() -> Component.literal(msg), true);
    }

    private NexusRPGCommand() {}
}
