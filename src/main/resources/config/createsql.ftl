CREATE TABLE `${tableName}` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  <#list fields as field>
   <#if field.fieldName!="id">
	  <#if field.fieldType!="datetime"&&field.fieldType!="mediumtext"&&field.fieldType!="double">
	  	`${field.fieldName}` ${field.fieldType}(#{field.fieldLength}),
	  <#else>
	  	`${field.fieldName}` ${field.fieldType},
	  </#if>
	</#if>
  </#list>
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;