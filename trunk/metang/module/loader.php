<?php

class loader extends PSX_ModuleAbstract
{
	private $sets = array(

		'default' => array('common', 'main', 'content_gadget', 'content_media', 'content_page', 'content_service', 'system_api', 'system_approval', 'system_country', 'system_event', 'system_log', 'system_vars', 'user_account', 'user_activity', 'user_friend', 'user_group', 'user_right'),
		'login'   => array('login'),
		'service' => array(/*'service_news', 'service_page'*/),

	);

	public function onLoad()
	{
		$script = '';
		$key    = isset($_GET['js']) ? $_GET['js'] : false;

		if(isset($this->sets[$key]))
		{
			$cache = 'cache/' . $key . '.js';

			if(!is_file($cache))
			{
				foreach($this->sets[$key] as $part)
				{
					$part = str_replace('_', '/', $part);
					$file = PSX_PATH_TEMPLATE . '/' . $this->config['psx_template_dir'] . '/js/amun/' . $part . '.js';

					$content = file_get_contents($file) . "\n\n";

					// minify js if not in debug mode
					if(!$this->config['psx_debug'])
					{
						$script.= JSMin::minify($content);
					}
					else
					{
						$script.= $content;
					}
				}

				// write cache if not in debug mode
				if(!$this->config['psx_debug'])
				{
					$handle = fopen($cache, 'w');
					fwrite($handle, $script);
					fclose($handle);
				}
			}
			else
			{
				$script = file_get_contents($cache);
			}
		}


		header('Content-type: text/javascript');

		echo $script;
	}
}


