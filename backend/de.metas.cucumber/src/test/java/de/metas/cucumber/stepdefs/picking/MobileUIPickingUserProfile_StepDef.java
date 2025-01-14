/*
 * #%L
 * de.metas.cucumber
 * %%
 * Copyright (C) 2023 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package de.metas.cucumber.stepdefs.picking;

import de.metas.cucumber.stepdefs.DataTableUtil;
import de.metas.handlingunits.picking.job.service.CreateShipmentPolicy;
import de.metas.logging.LogManager;
import de.metas.picking.config.MobileUIPickingUserProfile;
import de.metas.picking.config.MobileUIPickingUserProfileRepository;
import de.metas.util.StringUtils;
import de.metas.util.collections.CollectionUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import lombok.NonNull;
import org.compiere.SpringContextHolder;
import org.slf4j.Logger;

import java.util.Map;

public class MobileUIPickingUserProfile_StepDef
{
	private static final Logger logger = LogManager.getLogger(MobileUIPickingUserProfile_StepDef.class);
	private final MobileUIPickingUserProfileRepository repo = SpringContextHolder.instance.getBean(MobileUIPickingUserProfileRepository.class);

	@And("set mobile UI picking profile")
	public void updateProfile(@NonNull final DataTable dataTable)
	{
		final MobileUIPickingUserProfile.MobileUIPickingUserProfileBuilder newProfileBuilder = repo.getProfile().toBuilder();

		final Map<String, String> row = CollectionUtils.singleElement(dataTable.asMaps());
		StringUtils.trimBlankToOptional(DataTableUtil.extractNullableStringForColumnName(row, "IsAllowPickingAnyHU"))
				.map(StringUtils::toBoolean)
				.ifPresent(newProfileBuilder::isAllowPickingAnyHU);
		StringUtils.trimBlankToOptional(DataTableUtil.extractNullableStringForColumnName(row, "CreateShipmentPolicy"))
				.map(CreateShipmentPolicy::ofCodeOrName)
				.ifPresent(newProfileBuilder::createShipmentPolicy);

		final MobileUIPickingUserProfile newProfile = newProfileBuilder.build();
		repo.save(newProfile);
		logger.info("Profile updated: {}", newProfile);
	}
}
