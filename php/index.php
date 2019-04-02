<?php

require 'server.php';

class Comic extends Database {

   private $table = 'toc';

   /**
    * lấy danh sách truyện
    * nếu truyền $id > 0 thì lấy các chương của truyện có id = $id
    */
   function getToC($id = 0) {

      if ($id)
         $q = "SELECT id, title from toc where parent_id = $id";
      else
         $q = "SELECT id, title, name as category, cover, description from toc JOIN category using(cid) order by title";

      $data = $this->fetch($q);
      echo json_encode($data);
   }

   /** lấy các trang truyện từ chapter_id */
   function getPages($chapId) {

      $data = $this->fetch("SELECT id, content FROM `pages` WHERE chapter_id = $chapId");
      return json_encode($data);
   }

   function getCategories() {

      $data = $this->fetch("SELECT cid, name from category order by name");
      echo json_encode($data);
   }

   function getComicsByCategory($cid) {

      $data = $this->fetch("SELECT id, title, name as category, cover from toc JOIN category using(cid) where cid = $cid order by title");
      echo json_encode($data);
   }

   function searchComics($title) {

      $data = $this->fetch("SELECT id, title, name as category, cover, description from toc JOIN category using(cid) where title like '%$title%' order by title");
      echo json_encode($data);
   }

   function download($uri) {

      if (intval($uri) === 0) {

         preg_match('/(uploads.+)/', $uri, $uri);

         if (file_exists($uri[0])) {

            header('Content-Type: application/octet-stream');
            header('Content-Disposition: attachment; filename="'.basename($uri[0]).'"');
            header('Content-Length: ' . filesize($uri[0]));
            flush();
            readfile($uri[0]);
         }
         else echo json_encode(null);         
      }
      else {
         $data = json_decode($this->getPages($uri));

         $zipname = "chap.zip";
         $zip = new ZipArchive;
         $zip->open($zipname, ZipArchive::CREATE);
         for ($i = 0; $i < count($data); $i++) { 
            $zip->addFile($data[$i]->content);
         }
         $zip->close();

         header('Content-Type: application/zip');
         header('Content-disposition: attachment; filename='.$zipname);
         header('Content-Length: ' . filesize($zipname));
         readfile($zipname);

         ignore_user_abort(true);
         unlink($zipname);
      }
   }
}

header('content-type: application/json');
$comic = new Comic();

switch ($_SERVER['REQUEST_METHOD']) {
   case 'GET':
   if ( !empty($_SERVER['QUERY_STRING']) ) {

      if ( isset($_GET['toc']) )
         $comic->getToC($_GET['toc']);
      else if ( isset($_GET['chapid']) && trim($_GET['chapid']) > 0 )
         echo $comic->getPages($_GET['chapid']);
      else if ( isset($_GET['category']) )
         $comic->getCategories();
      else if ( isset($_GET['cid']) && trim($_GET['cid']) > 0 )
         $comic->getComicsByCategory($_GET['cid']);
      else if ( isset($_GET['search']) && trim($_GET['search']) != '' )
         $comic->searchComics($_GET['search']);
      else if ( isset($_GET['image']) && trim($_GET['image']) != '' )
         $comic->download($_GET['image']);
      else {
         http_response_code(400);
         echo http_response_code();
      }        
   }
   break;
   case 'POST':
   break;
}