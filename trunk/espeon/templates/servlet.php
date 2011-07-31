<?php

class ${namespace}_${name}_Servlet extends Amun_Data_ServletAbstract
{
	public function __construct(PSX_Config $config, PSX_Sql $sql, Amun_Vars $vars, $mode = 0)
	{
		parent::__construct($config, $sql, $vars, $mode);


		// assign fields
		$this->setDataFields(array(

			<#list fields as field>
			'${field}' => '`__ALIAS__`.`${field}`',
			</#list>

		));

		$this->setSupportedFields($this->getDataFields('${name?lower_case}'));


		// set default
		$this->setDefault(0, 32, 'id', 'DESC');
	}

	public function getQuery($sortBy, $sortOrder, $startIndex, $count)
	{
		return <<<SQL
SELECT

	{$this->getSelect()}

	FROM {$this->vars['table.${table}']} `${name?lower_case}`

		ORDER BY {$sortBy} {$sortOrder}

		LIMIT {$startIndex}, {$count}
SQL;
	}

	public function getTotalQuery()
	{
		return <<<SQL
SELECT

	COUNT(*) AS count

	FROM {$this->vars['table.${table}']} `${name?lower_case}`

		{$this->countCondition->getStatment()}
SQL;
	}
}

