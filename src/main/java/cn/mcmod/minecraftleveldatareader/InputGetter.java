package cn.mcmod.minecraftleveldatareader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InputGetter
{
    public static final String LEVEL_FILE_NAME = "level.dat";
    public static final String LEVEL_OLD_FILE_NAME = "level.dat_old";

    private final Path path;
    private String fileName;
    private InputType inputType;
    private Instant creationTime;

    public InputGetter(Path path)
    {
        this.path = path;
        this.creationTime = Instant.EPOCH;
        this.inputType = InputType.INDIVIDUAL;
        this.fileName = path.getFileName().toString();
    }

    public InputStream open(String fileName) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Path filePath = path.resolve(fileName);
            this.fileName = fileName;
            this.inputType = InputType.DIRECTORY;
            this.creationTime = Files.getLastModifiedTime(filePath).toInstant();
            return Files.newInputStream(filePath);
        }
        try
        {
            ZipFile zipFile = new ZipFile(path.toFile());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.equals(fileName) || entryName.endsWith("/" + fileName))
                {
                    this.fileName = fileName;
                    this.inputType = InputType.ZIP;
                    this.creationTime = Optional.ofNullable(entry.getCreationTime()).map(FileTime::toInstant).orElse(Instant.EPOCH);
                    return zipFile.getInputStream(entry);
                }
            }
            this.fileName = path.getFileName().toString();
            this.inputType = InputType.INDIVIDUAL;
            this.creationTime = Files.getLastModifiedTime(path).toInstant();
            return Files.newInputStream(path);
        }
        catch (IOException e)
        {
            this.fileName = path.getFileName().toString();
            this.inputType = InputType.INDIVIDUAL;
            this.creationTime = Files.getLastModifiedTime(path).toInstant();
            return Files.newInputStream(path);
        }
    }

    public InputType getInputType()
    {
        return this.inputType;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public Instant getCreationTime()
    {
        return this.creationTime;
    }
}
