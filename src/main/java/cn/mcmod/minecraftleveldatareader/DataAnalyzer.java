package cn.mcmod.minecraftleveldatareader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.querz.nbt.tag.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataAnalyzer
{
    private final CompoundTag tag;

    public DataAnalyzer(Tag<?> tag)
    {
        this.tag = tag instanceof CompoundTag ? ((CompoundTag) tag) : new CompoundTag();
    }

    public JsonObject toJsonObject()
    {
        JsonObject apiObject = new JsonObject();
        JsonArray modListArray = new JsonArray();
        CompoundTag fmlTag = this.getAsCompound(this.get(this.tag, "FML"));
        List<String> apiList = Arrays.asList("mcp", "fml", "forge"); // TODO: fabric?
        for (CompoundTag childTag : this.getAsList(this.get(fmlTag, "ModList"), CompoundTag.class))
        {
            Tag<?> modIdTag = this.get(childTag, "ModId");
            Tag<?> modVersionTag = this.get(childTag, "ModVersion");
            if (modIdTag instanceof StringTag && modVersionTag instanceof StringTag)
            {
                String modId = ((StringTag) modIdTag).getValue().toLowerCase(Locale.ENGLISH);
                int priority = apiList.indexOf(modId);
                if (priority < 0)
                {
                    JsonObject childObject = new JsonObject();
                    childObject.add("modid", new JsonPrimitive(modId));
                    childObject.add("version", new JsonPrimitive(((StringTag) modVersionTag).getValue()));
                    modListArray.add(childObject);
                }
                else if (!apiObject.has("name") || priority > apiList.indexOf(apiObject.get("name").getAsString()))
                {
                    apiObject.add("name", new JsonPrimitive(modId));
                    apiObject.add("version", new JsonPrimitive(((StringTag) modVersionTag).getValue()));
                }
            }
        }

        JsonObject result = new JsonObject();
        result.add("api", apiObject);
        result.add("modlist", modListArray);
        result.add("mod_count", new JsonPrimitive(modListArray.size())); // I don't know why it has underline

        return result;
    }

    private Tag<?> get(CompoundTag tag, String name)
    {
        return tag.containsKey(name) ? tag.get(name) : tag.keySet().stream().filter(name::equalsIgnoreCase).findFirst().<Tag<?>>map(tag::get).orElse(EndTag.INSTANCE);
    }

    private <T extends Tag<?>> ListTag<T> getAsList(Tag<?> tag, Class<T> cls)
    {
        return (tag instanceof ListTag && ((ListTag<?>) tag).getTypeClass() == cls ? (ListTag<?>) tag : ListTag.createUnchecked(cls)).asTypedList(cls);
    }

    private CompoundTag getAsCompound(Tag<?> tag)
    {
        return tag instanceof CompoundTag ? ((CompoundTag) tag) : new CompoundTag();
    }
}
