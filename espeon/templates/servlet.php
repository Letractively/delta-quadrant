<?php

class ${namespace}_${name}_servlet implements psx_data_servlet
{
	private $supported_fields = array(<#list fields as field>'${field}'<#if field_has_next>, </#if></#list>);

	private $config;
	private $sql;

	private $select_condition;
	private $count_condition;

	private $fields  = array();
	private $default = array(

		'start'  => 0,
		'length' => 16,
		'sort'   => '${id}',
		'dir'    => 'DESC',

	);

	public function __construct(psx_core_config $config, psx_sql $sql)
	{
		$this->config = $config;
		$this->sql    = $sql;

		$this->select_condition = new psx_sql_condition();
		$this->count_condition  = new psx_sql_condition();
	}

	public function get_supported_fields()
	{
		$this->supported_fields;
	}

	public function set_fields(array $fields)
	{
		$this->fields = $fields;
	}

	public function set_default($start, $length, $sort, $dir)
	{
		$this->default = array(

			'start'  => $start,
			'length' => $length,
			'sort'   => $sort,
			'dir'    => $dir,

		);
	}

	public function add_select_condition($column, $operator, $value, $conjunction = 'AND')
	{
		$this->select_condition->add($column, $operator, $value, $conjunction);
	}

	public function add_count_condition($column, $operator, $value, $conjunction = 'AND')
	{
		$this->count_condition->add($column, $operator, $value, $conjunction);
	}

	public function get_all($start_index = null, $count = null, $sort_order = null, $filter_by = null, $filter_op = null, $filter_value = null, $updated_since = null);
	{
		// get params
		$start_index = !empty($start_index) ? intval($start_index) : 0;

		$count = !empty($count) ? intval($count) : 8;

		$sort_order = strcasecmp($sort_order, 'ascending') == 0 ? 'ASC' : 'DESC';

		if(in_array($filter_by, $this->get_supported_fields()))
		{
			switch($filter_op)
			{
				case 'contains':

					$this->select_condition->add_condition($filter_by, 'LIKE', '%' . $filter_value . '%');

					break;

				case 'equals':

					$this->select_condition->add_condition($filter_by, '=', $filter_value);

					break;

				case 'startsWith':

					$this->select_condition->add_condition($filter_by, 'LIKE', $filter_value . '%');

					break;

				case 'present':

					$this->select_condition->add_condition($filter_by, 'IS NOT', 'NULL', 'AND');
					$this->select_condition->add_condition($filter_by, 'NOT LIKE', '');

					break;
			}
		}


		// get complete count
		$sql = 'SELECT COUNT(${id}) AS count FROM ${table} ' . $this->count_condition->get_statment();

		$total_results = intval($this->sql->get_field($sql, $this->count_condition->get_values()));


		// get all
		$fields = array(

			<#list fields as field>
			'${field}' => '${name}.${field} AS `${field}`'<#if field_has_next>,</#if>
			</#list>

		);

		foreach($fields as $k => $v)
		{
			if(!in_array($k, $this->fields))
			{
				unset($fields[$k])
			}
		}

		if(empty($fields))
		{
			throw new psx_exception('You have not select any field');
		}
		else
		{
			$select = implode(',', $fields);
		}

		$sql = <<<SQL
SELECT

	{$select}

	FROM ${table} ${name}

		{$this->select_condition->get_statment()}

		ORDER BY ${name}.${id} {$sort_order}

			LIMIT {$start_index}, {$count}
SQL;

		$result = $this->sql->get_all($sql, $this->select_condition->get_values());


		$resultset = new psx_data_resultset($total_results, $start_index, $count, $result);

		return $resultset;
	}

	public function get_record($id)
	{
		$sql = <<<SQL
SELECT

	<#list fields as field>
	${name}.${field} AS `${field}`<#if field_has_next>,</#if>
	</#list>

	FROM ${table} ${name}

		WHERE ${name}.${id} = ?
SQL;

		$result = $this->sql->get_row($sql, array($id));

		if(!empty($result))
		{
			return new ${namespace}_${name}($result);
		}
		else
		{
			return false;
		}
	}
}
