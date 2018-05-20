package me.t4tu.rkmobs.particles;

public enum ParticleEffectID {
	
	BLOOD(BloodParticleEffect.class);
	
	private Class<?> handler;
	
	private ParticleEffectID(Class<?> handler) {
		this.handler = handler;
	}
	
	public ParticleEffect newHandler() {
		try {
			return (ParticleEffect) handler.getDeclaredConstructor().newInstance();
		}
		catch (Exception e) { }
		return null;
	}
}