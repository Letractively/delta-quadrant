<?php
/*
 *  $Id: index.php 271 2011-04-16 20:05:57Z k42b3.x $
 *
 * psx
 * A object oriented and modular based PHP framework for developing
 * dynamic web applications. For the current version and informations
 * visit <http://phpsx.org>
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of psx. psx is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * psx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with psx. If not, see <http://www.gnu.org/licenses/>.
 */

require_once('../library/PSX/Config.php');
require_once('../library/PSX/Bootstrap.php');

$config    = new PSX_Config('../configuration.php');
$bootstrap = new PSX_Bootstrap($config);

try
{
	// load core libraries
	$registry = new PSX_Registry();
	$template = new PSX_Template($config);
	$loader   = new PSX_Loader($config, $registry);


	// add routes
	//$loader->addRoute('/.well-known/host-meta', 'sample');


	// add core libraries to the registry
	$registry->set($config);
	$registry->set($loader);
	$registry->set($template);


	ob_start();


	// prolog modules
	// $loader->load('psx/sql');


	// load module
	loadModule($config, $loader);


	// epilog modules
	// $loader->load('foo/bar');


	$content = ob_get_contents();

	ob_end_clean();


	// build response
	$response = buildResponse($config, $template, $content);

	echo $response;


	// cache handling
	if($config['psx_cache_enabled'] === true && $registry->has('cache') && $registry['cache']->write === true)
	{
		$registry['cache']->write($response);
	}
}
catch(Exception $e)
{
	if($config['psx_debug'] === true)
	{
		$debug = 'version: ' . $config['psx_version'] . "\n";
		$debug.= 'message: ' . $e->getMessage() . "\n";
		$debug.= 'code: ' . $e->getCode() . "\n";
		$debug.= 'file: ' . $e->getFile() . "\n";
		$debug.= 'line: ' . $e->getLine() . "\n";
		$debug.= 'exception: ' . get_class($e) . "\n";
		$debug.= $e->getTraceAsString();

		$template->assign('debug', $debug);
	}


	$template->assign('message', $e->getMessage());

	$template->set('system/exception.tpl');


	// build response
	echo buildResponse($config, $template);
}


/**
 * import
 *
 * @return void
 */
function import($x)
{
	global $loader;

	$loader->load($x);
}


/**
 * buildResponse
 *
 * @param psx_template $template
 * @return string
 */
function buildResponse(PSX_Config $config, PSX_Template $template, $content = NULL)
{
	if(empty($content))
	{
		if($template->load() === false)
		{
			throw new PSX_Exception('Could not load template');
		}


		if(!($response = $template->transform()))
		{
			throw new PSX_Exception('Error while transforming template');
		}


		$accept_encoding = PSX_Config::getRequestHeader('accept-encoding');

		if($config['psx_gzip'] === true && strpos($accept_encoding, 'gzip') !== false)
		{
			header('Content-Encoding: gzip');

			$response = gzencode($response, 9);
		}
	}
	else
	{
		$response = $content;
	}

	return $response;
}


/**
 * loadModule
 *
 * Loads the requested module depending on the psx_module_input field from the
 * config
 *
 * @return void
 */
function loadModule(PSX_Config $config, PSX_Loader $loader)
{
	$default = $config['psx_module_default'];
	$input   = $config['psx_module_input'];
	$length  = $config['psx_module_input_length'];

	if(!empty($input))
	{
		$x = $input;
	}
	else
	{
		$x = $default;
	}

	if(strpos($x, '..') !== false)
	{
		throw new PSX_Exception('Invalid signs in input');
	}

	if($length != 0)
	{
		if(strlen($x) > $length)
		{
			header('HTTP/1.1 414 Request-URI Too Long');

			throw new PSX_Exception('Max length of input is ' . $length);
		}
	}

	$loader->load($x);
}


