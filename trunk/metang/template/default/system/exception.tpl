<?php echo '<?xml version="1.0" encoding="UTF-8"?>' . "\n"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Metang - Exception</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="content-style-type" content="text/css" />
	<meta name="generator" content="psx" />
	<link rel="stylesheet" type="text/css" href="<?php echo $base ?>/css/common.css" />
	<link rel="icon" href="<?php echo $base ?>/img/favicon.ico" type="image/x-icon" />
</head>
<body>


<div class="container">

	<div class="body">

		<div class="box">

			<h3>An internal error has occurred</h3>

			<h4>Error:</h4>

			<p><?php echo $message; ?></p>

			<?php if(isset($debug)): ?>

				<div id="detail"><pre class="error"><?php echo $debug; ?></pre></div>

			<?php endif; ?>

		</div>

	</div>

</div>


</body>
</html>