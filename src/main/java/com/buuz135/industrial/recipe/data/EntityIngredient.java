package com.buuz135.industrial.recipe.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public record EntityIngredient(@Nullable EntityType<?> entityType, @Nullable TagKey<EntityType<?>> tag) implements Predicate<EntityType<?>> {

    public static final EntityIngredient EMPTY = new EntityIngredient(null, null);

    public static final EntityIngredient ANY = new EntityIngredient(null, TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("any")));

    public static final Codec<EntityIngredient> CODEC = Codec.either(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").codec(),
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").codec()
    ).xmap(
            either -> either.map(EntityIngredient::of, EntityIngredient::of),
            ingredient -> {
                if (ingredient.entityType != null) {
                    return Either.left(ingredient.entityType);
                } else if (ingredient.tag != null) {
                    return Either.right(ingredient.tag);
                } else {
                    throw new IllegalStateException("Empty EntityIngredient cannot be serialized");
                }
            }
    );

    @Override
    public boolean test(@Nullable EntityType<?> entityType) {
        if (entityType == null || this.isEmpty()) {
            return false;
        }

        if (this.entityType != null) {
            return this.entityType.equals(entityType);
        } else {
            for (Holder<EntityType<?>> holder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(this.tag)) {
                if (holder.value().equals(entityType)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean test(@Nullable Entity entity) {
        if (this.equals(ANY)) return true;
        return entity != null && test(entity.getType());
    }

    public boolean isEmpty() {
        return this.entityType == null && this.tag == null;
    }

    public boolean isType() {
        return this.entityType != null;
    }

    public boolean isTag() {
        return this.tag != null;
    }

    @Nullable
    public EntityType<?> getType() {
        return entityType;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EntityIngredient(EntityType<?> type, TagKey<EntityType<?>> tag1))) return false;
        return Objects.equals(this.entityType, type) && Objects.equals(this.tag, tag1);
    }

    public static EntityIngredient of(EntityType<?> entityType) {
        return new EntityIngredient(entityType, null);
    }

    public static EntityIngredient of(TagKey<EntityType<?>> tag) {
        return new EntityIngredient(null, tag);
    }
}
