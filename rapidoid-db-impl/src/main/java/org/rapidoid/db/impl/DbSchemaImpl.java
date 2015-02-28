package org.rapidoid.db.impl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.db.DbDsl;
import org.rapidoid.db.DbSchema;
import org.rapidoid.db.IEntity;
import org.rapidoid.util.Cls;
import org.rapidoid.util.English;
import org.rapidoid.util.Scan;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-db-impl
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbSchemaImpl implements DbSchema {

	private final ConcurrentMap<String, Class<?>> entityTypes = U.concurrentMap();

	private final ConcurrentMap<String, Class<?>> entityTypesPlural = U.concurrentMap();

	public DbSchemaImpl() {
		for (Class<?> entityType : Scan.annotated(DbEntity.class)) {
			putEntityType(entityType);
		}
		for (Class<?> entityType : Scan.annotated(Scaffold.class)) {
			putEntityType(entityType);
		}
	}

	@Override
	public <E> DbDsl<E> dsl(Class<E> entityType) {
		putEntityType(entityType);

		return null; // FIXME implement this
	}

	private <E> void putEntityType(Class<E> entityType) {
		String type = entityType.getSimpleName().toLowerCase();

		entityTypes.putIfAbsent(type, entityType);
		entityTypesPlural.putIfAbsent(English.plural(type), entityType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityType(String typeName) {
		return (Class<E>) entityTypes.get(typeName.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityTypeFromPlural(String typeNamePlural) {
		return (Class<E>) entityTypesPlural.get(typeNamePlural.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E entity(Class<E> clazz, Map<String, ?> properties) {
		U.notNull(clazz, "entity class");
		Class<E> entityType = getEntityTypeFor(clazz);
		U.notNull(entityType, "entity type");
		if (entityType.isInterface() && IEntity.class.isAssignableFrom(entityType)) {
			Class<? extends IEntity> cls = (Class<? extends IEntity>) entityType;
			return (E) DbProxy.create(cls, properties);
		} else {
			E entity = Cls.newInstance(entityType);
			Beany.update(entity, (Map<String, Object>) properties, true);
			return entity;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityTypeFor(Class<E> clazz) {
		if (IEntity.class.isAssignableFrom(clazz)) {
			if (Proxy.class.isAssignableFrom(clazz)) {
				for (Class<?> interf : clazz.getInterfaces()) {
					if (IEntity.class.isAssignableFrom(interf)) {
						return (Class<E>) interf;
					}
				}
				throw U.rte("Cannot find entity type for: %s!", clazz);
			}
		}
		return clazz;
	}

	@Override
	public Object entity(String data) {
		String entityName = U.capitalized(data.split(" ")[0]);
		Class<?> entityType = getEntityType(entityName);
		U.must(entityType != null, "Cannot find entity '%s'!", entityName);

		String[] props = data.substring(entityName.length() + 1).split("\\s*\\,\\s*");
		Map<String, Object> properties = U.map();

		for (String prop : props) {
			String[] kv = prop.trim().split("\\s*=\\s*");
			String key = kv[0];
			Object value = kv.length > 1 ? kv[1] : true;
			properties.put(key, value);
		}

		return entity(entityType, properties);
	}

	@Override
	public String toString() {
		return "DbSchemaImpl [entityTypes=" + entityTypes + ", entityTypesPlural=" + entityTypesPlural + "]";
	}

}
