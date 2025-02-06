package net.elysium.VillageBuilder;

import net.elysium.VillageBuilder.Commands.CreateStructureCommand;
import net.elysium.VillageBuilder.Commands.PositionSelectionCommand;
import net.elysium.VillageBuilder.Commands.SelectionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {
    SelectionManager selectionManager = new SelectionManager();
    PositionSelectionCommand positionSelectionCommand = new PositionSelectionCommand(selectionManager);

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("pos")).setExecutor(positionSelectionCommand);
        Objects.requireNonNull(this.getCommand("createStructure")).setExecutor(new CreateStructureCommand(selectionManager, positionSelectionCommand, getDataFolder()));
    }
}
