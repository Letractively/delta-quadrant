<?php

class main extends PSX_ModuleAbstract
{
	public function onLoad()
	{
		$this->registry['Template']->set('main.tpl');
	}
}
