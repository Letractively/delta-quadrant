<?php

class ${namespace}_${name} extends psx_data_record
{
	public function get_name()
	{
		return '${name}';
	}

	<#list fields as field>
	public function set_${field}($${field})
	{
		$this->offsetSet('${field}', $${field});
	}

	</#list>
	public static function convert(array $data)
	{
	}
}
