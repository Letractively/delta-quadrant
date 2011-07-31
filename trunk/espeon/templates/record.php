<?php

class ${namespace}_${name} extends Amun_Data_RecordAbstract
{
	protected $fields = array(

		<#list fields as field>
		'${field}',
		</#list>

	);

	public function getName()
	{
		return '${name?lower_case}';
	}

	<#list fields as field>
	public function set${field?cap_first}($${field})
	{
		$this->${field} = $${field};
	}

	</#list>
}
