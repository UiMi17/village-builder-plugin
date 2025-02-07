package net.elysium.VillageBuilder.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AssignStageCommand implements CommandExecutor {
    private final StructureManager structureManager;

    public AssignStageCommand(StructureManager structureManager) {
        this.structureManager = structureManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length < 2) {
            player.sendMessage("Використання: /assignStructureStage [назва_стадії] [назва_структури]");
            return false;
        }

        String stageName = args[0];
        String structureName = args[1];

        structureManager.addStructureStage(player, stageName, structureName);
        return true;
    }
}
