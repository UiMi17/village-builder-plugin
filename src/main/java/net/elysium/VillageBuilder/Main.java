package net.elysium.VillageBuilder;

import net.elysium.VillageBuilder.Commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {
    SelectionManager selectionManager = new SelectionManager();
    PositionSelectionCommand positionSelectionCommand = new PositionSelectionCommand(selectionManager);
    StructureManager structureManager = new StructureManager(selectionManager, positionSelectionCommand);

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("pos")).setExecutor(positionSelectionCommand);
        Objects.requireNonNull(getCommand("create-structure")).setExecutor(new CreateStructureCommand(selectionManager, positionSelectionCommand, getDataFolder()));
        Objects.requireNonNull(getCommand("assign-stage")).setExecutor(new AssignStageCommand(structureManager));
        Objects.requireNonNull(getCommand("assign-resources")).setExecutor(new ResourceAssignmentCommand());
    }
}
