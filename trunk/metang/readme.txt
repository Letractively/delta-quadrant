
	+ Configuration

	You have to insert into the configuration.php the metang_consumer_key and
	metang_consumer_secret wich you get when installing Amun. If you dont have
	received the consumerKey and consumerSecret you can take a look at the table
	"amun_system_api" in the database where you have installed Amun. There
	should be an entry with a consumerKey and consumerSecret.


	+ OAuth authentication

	Note if you authenticate metang tries to write the token and toke secret
	into the cache dir so make sure it has write permissions.
