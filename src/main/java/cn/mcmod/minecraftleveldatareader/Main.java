/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package cn.mcmod.minecraftleveldatareader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Main
{
    public static void scan(Path path)
    {
        InputGetter getter = new InputGetter(path);
        NBTDeserializer deserializer = new NBTDeserializer(true);
        try
        {
            NamedTag tag = deserializer.fromStream(getter.open(InputGetter.LEVEL_FILE_NAME));
            Main.print(tag, getter.getInputType(), getter.getFileName(), getter.getCreationTime());
        }
        catch (IOException e)
        {
            String fileName = getter.getFileName();
            InputType inputType = getter.getInputType();
            Instant creationTime = getter.getCreationTime();
            try
            {
                NamedTag tag = deserializer.fromStream(getter.open(InputGetter.LEVEL_OLD_FILE_NAME));
                Main.print(tag, getter.getInputType(), getter.getFileName(), getter.getCreationTime());
            }
            catch (IOException ignored)
            {
                Main.error(e, inputType, fileName, creationTime);
            }
        }
    }

    public static void print(NamedTag tag, InputType inputType, String fileName, Instant creationTime)
    {
        String time = DateTimeFormatter.ISO_INSTANT.format(creationTime);
        Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
        JsonObject objectForOutput = new DataAnalyzer(tag.getTag()).toJsonObject();

        objectForOutput.add("creation_time", new JsonPrimitive(time));
        objectForOutput.add("file_name", new JsonPrimitive(fileName));
        objectForOutput.add("input_type", new JsonPrimitive(inputType.toString().toLowerCase(Locale.ENGLISH)));

        gson.toJson(objectForOutput, System.out);
        System.out.println();
        System.out.flush();
    }

    public static void error(IOException e, InputType inputType, String fileName, Instant creationTime)
    {
        String time = DateTimeFormatter.ISO_INSTANT.format(creationTime);

        Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
        JsonObject objectForOutput = new JsonObject();

        objectForOutput.add("error", new JsonPrimitive(e.toString()));
        objectForOutput.add("creation_time", new JsonPrimitive(time));
        objectForOutput.add("file_name", new JsonPrimitive(fileName));
        objectForOutput.add("input_type", new JsonPrimitive(inputType.toString().toLowerCase(Locale.ENGLISH)));

        gson.toJson(objectForOutput, System.out);
        System.out.println();
        System.out.flush();
    }

    public static void help(String version, String description, String website)
    {
        System.out.println("Usage: java -jar MinecraftLevelDataReader-" + version + ".jar [FILE]");
        System.out.println(description + ".");
        System.out.println();
        System.out.println("The FILE argument can be path to a directory which contains 'level.dat', a zip");
        System.out.println("archive which contains 'level.dat', or 'level.dat' itself.");
        System.out.println();
        System.out.println("Full documentation at: <" + website + ">");
    }

    public static void main(String[] args)
    {
        if (args.length == 0 || args.length == 1 && ("-h".equals(args[0]) || "--help".equals(args[0])))
        {
            Main.help("@version@", "@description@", "@website@");
        }
        else
        {
            Main.scan(Paths.get(String.join(" ", args)));
        }
    }
}