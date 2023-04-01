package cum.jesus.jesusclient.utils.slaves.jobs;

import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.slaves.Job;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class DownloadJob extends Job {
    private final String url;
    private final Path targetLocation; // full file not just folder
    private final Consumer<DynamicTexture> callback;

    public DownloadJob(int jobId, String url, Path targetLocation, Consumer<DynamicTexture> callback) {
        super(jobId);
        this.url = url;
        this.targetLocation = targetLocation;
        this.callback = callback;
    }

    public DownloadJob(int jobId, String url, File targetLocation, Consumer<DynamicTexture> callback) {
        super(jobId);
        this.url = url;
        this.targetLocation = targetLocation.toPath();
        this.callback = callback;
    }

    public DownloadJob(int jobId, String url, String targetLocation, Consumer<DynamicTexture> callback) {
        super(jobId);
        this.url = url;
        this.targetLocation = new File(targetLocation).toPath();
        this.callback = callback;
    }

    @Override
    public void run() {
        Logger.info("Starting job " + jobId + ": \"Download " + url + "\"");
        try {
            Files.copy(new URL(url).openStream(), targetLocation);
            BufferedImage image = ImageIO.read(targetLocation.toFile());
            DynamicTexture texture = new DynamicTexture(image);
            callback.accept(texture);
        } catch (IOException e) {
            throw new RuntimeException("Exception caused by job " + jobId, e);
        } finally {
            Logger.info("Finished job " + jobId);
        }
    }
}
