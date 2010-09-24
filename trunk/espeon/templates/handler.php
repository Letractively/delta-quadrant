<?php

class ${namespace}_${name}_handler implements psx_data_handler
{
	private $config;
	private $sql;

	public function __construct(psx_core_config $config, psx_sql $sql)
	{
		$this->config = $config;
		$this->sql    = $sql;
	}

	public function create(psx_data_record $record)
	{
	}

	public function update(psx_data_record $record)
	{
	}

	public function delete(psx_data_record $record)
	{
	}
}