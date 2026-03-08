package com.causmax22.tow_nuclear_dawn.registry.event.radiation;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

@EventBusSubscriber(modid = ToWNuclearDawn.MODID)
public class ResetDoseCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // /resetdose
        event.getDispatcher().register(
                Commands.literal("resetdose")
                        .executes(ctx -> resetSelf(ctx))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .requires(src -> src.hasPermission(2))
                                .executes(ctx -> resetTargets(ctx))
                        )
        );

        // /removesources — clears ALL radiation sources (op only)
        event.getDispatcher().register(
                Commands.literal("removesources")
                        .requires(src -> src.hasPermission(2))
                        .executes(ctx -> removeSources(ctx))
        );
    }

    private static int resetSelf(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            DoseCapabilityProvider.get(player).reset();
            ctx.getSource().sendSuccess(
                    () -> Component.literal("§a✔ Your accumulated dose has been reset to 0 Srd."), false
            );
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cCould not reset dose: " + e.getMessage()));
            return 0;
        }
    }

    private static int resetTargets(CommandContext<CommandSourceStack> ctx) {
        try {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
            for (ServerPlayer target : targets) {
                DoseCapabilityProvider.get(target).reset();
                target.sendSystemMessage(Component.literal("§a✔ Your accumulated dose has been reset by an operator."));
            }
            ctx.getSource().sendSuccess(
                    () -> Component.literal("§a✔ Reset dose for " + targets.size() + " player(s)."), true
            );
            return targets.size();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cCould not reset dose: " + e.getMessage()));
            return 0;
        }
    }

    private static int removeSources(CommandContext<CommandSourceStack> ctx) {
        try {
            int count = Radiation.getActiveSources().size();
            Radiation.getActiveSources().clear();

            // Persist the clear to SavedData
            if (ctx.getSource().getLevel() instanceof ServerLevel serverLevel) {
                Radiation.RadiationSavedData.get(serverLevel).setSources(Radiation.getActiveSources());
                Radiation.RadiationSavedData.get(serverLevel).setDirty();
            }

            ctx.getSource().sendSuccess(
                    () -> Component.literal("§a✔ Removed all " + count + " radiation sources."), true
            );
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cCould not remove sources: " + e.getMessage()));
            return 0;
        }
    }
}