<?php

Table: ${table}
First column: ${first_column}
Last column: ${last_column}
Primary key: ${primary_key}
Unqiue key: <#list unqiue_key as key>${key}<#if key_has_next>, </#if></#list>
Fields: <#list fields as field>${field}<#if field_has_next>, </#if></#list>

Columns:
<#list columns as column>

	Field: ${column.field}
	Type: ${column.type}
	Length: ${column.length}
	Null: ${column.null}
	Key: ${column.key}
	Default: ${column.default}
	Extra: ${column.extra}

</#list>
