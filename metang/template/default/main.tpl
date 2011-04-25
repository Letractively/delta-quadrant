<html>
<head id="head">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title id="title">Amun</title>

	<!-- css -->
	<link rel="stylesheet" type="text/css" href="<?php echo $base; ?>/js/ext/resources/css/ext-all-notheme.css" />
	<link rel="stylesheet" type="text/css" href="<?php echo $base; ?>/js/ext/resources/css/xtheme-gray.css" />
	<link rel="stylesheet" type="text/css" href="<?php echo $base; ?>/css/common.css" />

	<!-- ext -->
	<script type="text/javascript" defer="1" src="<?php echo $base; ?>/js/ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" defer="1" src="<?php echo $base; ?>/js/ext/ext-all-debug.js"></script>

	<script type="text/javascript">
	<!--
	var amun_url = '<?php echo $config['metang_url']; ?>';
	var metang_url = '<?php echo $url; ?>';

	function get_proxy_url(url, params)
	{
		var param = '';

		for(k in params)
		{
			param+= '&' + k + '=' + encodeURIComponent(params[k]);
		}

		return 'proxy.php?url=' + encodeURIComponent(url) + param;
	}
	-->
	</script>

	<script type="text/javascript" defer="1" src="<?php echo $url; ?>loader?js=default"></script>
	<script type="text/javascript" defer="1" src="<?php echo $url; ?>loader?js=service"></script>
</head>
<body>

	<div id="loading_splash" class="loading_splash"></div>

</body>
</html>