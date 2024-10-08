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

package com.morpheusdata.nutanix.prismelement.plugin.backup

import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.backup.MorpheusBackupProvider

class NutanixPrismElementBackupProvider extends MorpheusBackupProvider {
	NutanixPrismElementBackupProvider(Plugin plugin, MorpheusContext morpheusContext) {
		super(plugin, morpheusContext)

		NutanixPrismElementBackupTypeProvider nutanixPrismSnapshotProvider = new NutanixPrismElementBackupTypeProvider(plugin, morpheusContext)
		plugin.registerProvider(nutanixPrismSnapshotProvider)
		addScopedProvider(nutanixPrismSnapshotProvider, "nutanix", null)
	}
}
