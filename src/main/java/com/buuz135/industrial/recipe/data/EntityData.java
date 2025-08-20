package com.buuz135.industrial.recipe.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntityData {
    
    private final EntityIngredient ingredient;
    private final CompoundTag tag;
    private final Component display;

    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityIngredient.CODEC.fieldOf("entity").forGetter(o -> o.ingredient),
            CompoundTag.CODEC.fieldOf("data").forGetter(o -> o.tag),
            ComponentSerialization.CODEC.fieldOf("display").forGetter(o -> o.display)
    ).apply(instance, EntityData::new));

    private EntityData(EntityIngredient ingredient, CompoundTag data, Component display) {
        this.ingredient = ingredient;
        this.tag = data;
        this.display = display;
    }
    
    public static EntityData of(EntityType<?> type) {
        return new EntityData(EntityIngredient.of(type), new CompoundTag(), Component.empty());
    }

    public static EntityData of(TagKey<EntityType<?>> tag) {
        return new EntityData(EntityIngredient.of(tag), new CompoundTag(), Component.empty());
    }
    
    public static TagBuilder builder() {
        return new TagBuilder();
    }

    public EntityIngredient getEntity() {
        return ingredient;
    }
    
    public Component getDisplay() {
        return display;
    }

    public CompoundTag getData() {
        return tag;
    }

    public static class TagBuilder {
        
        private final CompoundTag tag;

        protected TagBuilder() {
            this.tag = new CompoundTag();
        }

        public TagBuilder putTag(String key, Function<CompoundTag, Tag> tagBuilder) {
            Tag value = tagBuilder.apply(new CompoundTag());
            tag.put(key, value);
            return this;
        }

        public TagBuilder putNested(String key, Consumer<TagBuilder> builderConsumer) {
            TagBuilder nestedBuilder = new TagBuilder();
            builderConsumer.accept(nestedBuilder);
            tag.put(key, nestedBuilder.tag);
            return this;
        }

        public TagBuilder putByte(String key, byte value) {
            tag.putByte(key, value);
            return this;
        }

        public TagBuilder putShort(String key, short value) {
            tag.putShort(key, value);
            return this;
        }

        public TagBuilder putInt(String key, int value) {
            tag.putInt(key, value);
            return this;
        }

        public TagBuilder putLong(String key, long value) {
            tag.putLong(key, value);
            return this;
        }

        public TagBuilder putUUID(String key, UUID value) {
            tag.putUUID(key, value);
            return this;
        }

        public TagBuilder putFloat(String key, float value) {
            tag.putFloat(key, value);
            return this;
        }

        public TagBuilder putDouble(String key, double value) {
            tag.putDouble(key, value);
            return this;
        }

        public TagBuilder putString(String key, String value) {
            tag.putString(key, value);
            return this;
        }

        public TagBuilder putByteArray(String key, byte[] value) {
            tag.putByteArray(key, value);
            return this;
        }

        public TagBuilder putByteArray(String key, List<Byte> value) {
            tag.putByteArray(key, value);
            return this;
        }

        public TagBuilder putIntArray(String key, int[] value) {
            tag.putIntArray(key, value);
            return this;
        }

        public TagBuilder putIntArray(String key, List<Integer> value) {
            tag.putIntArray(key, value);
            return this;
        }

        public TagBuilder putLongArray(String key, long[] value) {
            tag.putLongArray(key, value);
            return this;
        }

        public TagBuilder putLongArray(String key, List<Long> value) {
            tag.putLongArray(key, value);
            return this;
        }

        public TagBuilder putBoolean(String key, boolean value) {
            tag.putBoolean(key, value);
            return this;
        }

        public EntityData build(Component display, EntityIngredient ingredient) {
            return new EntityData(ingredient, tag, display);
        }
    }
}
