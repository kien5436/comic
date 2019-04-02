<?php

class Database {

	protected static $db = null;

	function __construct() {
		self::$db = new mysqli('localhost', 'root', '', 'comic');
		if (self::$db->connect_error)
			var_dump(self::$db->connect_error);
		else
			self::$db->set_charset('utf8');
	}

	protected function fetch($q) {
		
		$res = self::$db->query($q);
		$data = [];
		if ($res) {
			
			while ($row = $res->fetch_assoc())
				$data[] = $row;
			
			$res->free();
		}
      self::$db->close();

		return $data;
	}
}