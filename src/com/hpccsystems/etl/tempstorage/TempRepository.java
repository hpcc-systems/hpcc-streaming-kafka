package com.hpccsystems.etl.tempstorage;

public interface TempRepository {
	public abstract void save(byte[] data);
}
