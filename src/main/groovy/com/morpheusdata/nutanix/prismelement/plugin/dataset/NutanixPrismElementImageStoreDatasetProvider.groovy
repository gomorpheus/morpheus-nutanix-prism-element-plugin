/*
 * Copyright 2024 Morpheus Data, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.morpheusdata.nutanix.prismelement.plugin.dataset

import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.data.DataQuery
import com.morpheusdata.core.data.DatasetInfo
import com.morpheusdata.core.data.DatasetQuery
import com.morpheusdata.core.providers.AbstractDatasetProvider
import com.morpheusdata.model.Datastore
import io.reactivex.rxjava3.core.Observable

class NutanixPrismElementImageStoreDatasetProvider extends AbstractDatasetProvider<Datastore, String> {
	public static final PROVIDER_NAME = "Nutanix Image Store Provider"
	public static final PROVIDER_NAMESPACE = "com.morpheusdata.nutanix.prismelement.plugin"
	public static final PROVIDER_KEY = "nutanixContainers"
	public static final PROVIDER_DESCRIPTION = "The default image store to use with Nutanix Prism Element"

	NutanixPrismElementImageStoreDatasetProvider(Plugin plugin, MorpheusContext morpheusContext) {
		this.plugin = plugin
		this.morpheusContext = morpheusContext
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatasetInfo getInfo() {
		new DatasetInfo(
			name: PROVIDER_NAME,
			namespace: PROVIDER_NAMESPACE,
			key: PROVIDER_KEY,
			description: PROVIDER_DESCRIPTION,
		)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Datastore> getItemType() {
		return Datastore.class
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Observable<Datastore> list(DatasetQuery query) {
		Long cloudId = query.get("zoneId")?.toLong()
		morpheusContext.async.cloud.datastore.list(
			new DataQuery()
				.withFilter('refType', 'ComputeZone')
				.withFilter('refId', cloudId)
				.withFilter('type', 'generic')
		)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Observable<Map> listOptions(DatasetQuery query) {
		list(query).map { [name: it.name, value: it.externalId] }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Datastore fetchItem(Object value) {
		def rtn = null
		if (value instanceof String) {
			rtn = item((String) value)
		}
		return rtn

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Datastore item(String value) {
		def query = new DatasetQuery().withFilter("externalId", value)
		query.max = 1
		return list(query as DatasetQuery)
			.toList()
			.blockingGet()
			.first()
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String itemName(Datastore item) {
		return item.name
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String itemValue(Datastore item) {
		return item.id
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isPlugin() {
		return true
	}
}
