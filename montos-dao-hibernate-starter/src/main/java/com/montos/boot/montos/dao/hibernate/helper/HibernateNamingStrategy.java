package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.dao.hibernate.config.HibernateProperties;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Value;

public class HibernateNamingStrategy extends ImprovedNamingStrategy {

	private static final long serialVersionUID = 1L;

	private boolean under;

	@Value("${hibernate.nameStrategyUnder:false}")
	public void setUnder(boolean under) {
		this.under = under;
	}

	@Override
	public String classToTableName(String className) {
		if (under) {
			return super.classToTableName(className);
		}
		return className;
	}

	@Override
	public String propertyToColumnName(String propertyName) {
		if (under) {
			return super.propertyToColumnName(propertyName);
		}
		return propertyName;
	}

	@Override
	public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName,
			String referencedColumnName) {
		if (under) {
			return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName,
					referencedColumnName);
		}
		return propertyName != null ? StringHelper.unqualify(propertyName) : propertyTableName;
	}

	@Override
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		// TODO Auto-generated method stub
		return super.joinKeyColumnName(joinedColumn, joinedTable);
	}

	@Override
	public String tableName(String tableName) {
		if (under) {
			return super.tableName(tableName);
		}
		return tableName;
	}

	@Override
	public String columnName(String columnName) {
		if (under) {
			return super.columnName(columnName);
		}
		return columnName;
	}

	@Override
	public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity,
			String associatedEntityTable, String propertyName) {
		return super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable,
				propertyName);
	}

	@Override
	public String logicalColumnName(String columnName, String propertyName) {
		// TODO Auto-generated method stub
		return super.logicalColumnName(columnName, propertyName);
	}

	@Override
	public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable,
			String propertyName) {
		return super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName);
	}

	@Override
	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {

		if (under) {
			return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn);
		}

		return columnName;
	}

	public void setHibernateProperties(HibernateProperties hibernateProperties) {
		under = hibernateProperties.getNameStrategyUnder();
	}
}