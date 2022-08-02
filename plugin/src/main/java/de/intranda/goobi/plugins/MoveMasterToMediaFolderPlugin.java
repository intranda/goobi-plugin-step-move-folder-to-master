package de.intranda.goobi.plugins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.goobi.beans.Step;
import org.goobi.production.enums.PluginGuiType;
import org.goobi.production.enums.PluginReturnValue;
import org.goobi.production.enums.PluginType;
import org.goobi.production.enums.StepReturnValue;
import org.goobi.production.plugin.interfaces.IStepPluginVersion2;

import de.sub.goobi.helper.StorageProvider;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.SwapException;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@Log4j

public class MoveMasterToMediaFolderPlugin implements IStepPluginVersion2 {

    @Getter
    private String pagePath = "";

    @Getter
    private PluginGuiType pluginGuiType = PluginGuiType.NONE;

    @Getter
    private Step step;

    private String returnPath;

    @Getter
    private String title = "intranda_step_moveMasterToMedia";

    @Getter
    private PluginType type = PluginType.Step;

    @Getter
    private int interfaceVersion = 1;

    @Override
    public void initialize(Step step, String returnPath) {
        this.step = step;
        this.returnPath = returnPath;
    }

    @Override
    public HashMap<String, StepReturnValue> validate() {
        return null;
    }

    @Override
    public PluginReturnValue run() {
        if (execute()) {
            return PluginReturnValue.FINISH;
        } else {
            return PluginReturnValue.ERROR;
        }
    }

    @Override
    public String cancel() {
        return null;
    }

    @Override
    public boolean execute() {

        try {
            Path masterFolder = Paths.get(step.getProzess().getImagesOrigDirectory(false));
            Path mediaFolder = Paths.get(step.getProzess().getImagesTifDirectory(false));

            if (Files.exists(masterFolder) && !Files.exists(mediaFolder)) {
                Files.move(masterFolder, mediaFolder);
            } else {
                // check if media Folder is empty
                if (StorageProvider.getInstance().list(mediaFolder.toString()).isEmpty()) {
                    Files.delete(mediaFolder);
                    Files.move(masterFolder, mediaFolder);
                } else {
                    log.info("Folder " + mediaFolder + " already exists, do not overwrite");
                }
            }

        } catch (IOException | SwapException | DAOException e) {
            log.error(e);
            return false;
        }

        return true;
    }

    @Override
    public String finish() {
        return null;
    }
}
