<?php

class main extends PSX_ModuleAbstract
{
	public function onLoad()
	{
		$this->template->set('main.tpl');
	}
}
