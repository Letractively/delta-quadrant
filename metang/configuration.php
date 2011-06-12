<?php
/*
 *  $Id: configuration.php 270 2011-04-16 19:30:28Z k42b3.x $
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

$config = array(

	'metang_version'          => '0.0.1 beta',
	'metang_url'              => 'http://127.0.0.1/projects/amun/public/index.php/',

	'metang_consumer_key'     => 'b8858501073e5fb54e75b973ed044ec19f21a60d',
	'metang_consumer_secret'  => '07d8b5173afba2575e57ca5966624a39419b5b70',

	'metang_request'          => 'http://127.0.0.1/projects/amun/public/index.php/api/auth/request',
	'metang_authorization'    => 'http://127.0.0.1/projects/amun/public/index.php/api/auth/authorization',
	'metang_access'           => 'http://127.0.0.1/projects/amun/public/index.php/api/auth/access',

	'psx_version'             => '0.3.3',
	'psx_url'                 => 'http://127.0.0.1/projects/metang/public',
	'psx_dispatch'            => 'index.php/',
	'psx_timezone'            => 'Europe/Berlin',
	'psx_gzip'                => false,
	'psx_debug'               => true,

	'psx_module_default'      => 'main',
	'psx_module_input'        => isset($_GET['x']) ? $_GET['x'] : (isset($_SERVER['PATH_INFO']) ? $_SERVER['PATH_INFO'] : (isset($_SERVER['argv'][1]) ? $_SERVER['argv'][1] : '')),
	'psx_module_input_length' => 256,

	'psx_sql_host'            => 'localhost',
	'psx_sql_user'            => 'root',
	'psx_sql_pw'              => '',
	'psx_sql_db'              => 'psx',

	'psx_cache_enabled'       => true,
	'psx_cache_expire'        => 60 * 30,

	'psx_template_dir'        => 'default',
	'psx_template_default'    => false,

	'psx_path_cache'          => '../cache',
	'psx_path_library'        => '../library',
	'psx_path_module'         => '../module',
	'psx_path_template'       => '../template',

);

