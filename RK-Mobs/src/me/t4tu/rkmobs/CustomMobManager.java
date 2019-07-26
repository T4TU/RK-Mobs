package me.t4tu.rkmobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.DataConverterRegistry;
import net.minecraft.server.v1_14_R1.DataConverterTypes;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumCreatureType;
import net.minecraft.server.v1_14_R1.IRegistry;
import net.minecraft.server.v1_14_R1.SharedConstants;
import net.minecraft.server.v1_14_R1.World;

public class CustomMobManager {
	
	@SuppressWarnings("rawtypes")
	private List<EntityTypes> entityTypes;
	
	@SuppressWarnings("rawtypes")
	public CustomMobManager() {
		entityTypes = new ArrayList<EntityTypes>();
		registerEntities();
	}
	
	@SuppressWarnings("unchecked")
	private void registerEntities() {
		
		Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
		
		dataTypes.put("minecraft:custom_zombie", dataTypes.get("minecraft:zombie"));
		EntityTypes.a<Entity> a = EntityTypes.a.a(NonWanderingZombie::new, EnumCreatureType.MONSTER);
		entityTypes.add(IRegistry.a(IRegistry.ENTITY_TYPE, "custom_zombie", a.a("custom_zombie")));
	}
	
	public org.bukkit.entity.Entity spawnEntity(int index, Location location) {
		try {
			World world = ((CraftWorld) location.getWorld()).getHandle();
			Entity entity = entityTypes.get(index).b(world, null, null, null, new BlockPosition(location.getX(), location.getY(), location.getZ()), null, false, false);
			world.addEntity(entity, SpawnReason.CUSTOM);
			return entity.getBukkitEntity();
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}