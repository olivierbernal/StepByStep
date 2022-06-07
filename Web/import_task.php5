<?php

  define("colonne_1", "ref_rubrique");
  define("colonne_2", "n_tache");
  define("colonne_3", "tache");
  define("colonne_4", "description");

   if(isset($_FILES['csv'])){
      $errors= array();
      $file_name = $_FILES['csv']['name'];
      $file_size =$_FILES['csv']['size'];
      $file_tmp =$_FILES['csv']['tmp_name'];
      $file_type=$_FILES['csv']['type'];
      $file_ext=strtolower(end(explode('.',$_FILES['csv']['name'])));
      
      $extensions= array("csv");
      
      if(in_array($file_ext,$extensions)=== false){
         $errors[]="L'extension du fichier n'est pas correcte, il faut un fichier CSV.";
      }
      
      if($file_size > 10485760){
         $errors[]='Le fichier doit faire moins de 10 Mo';
      }
      
      if(empty($errors)==true){
         $csvFile = file($file_tmp);
         //remove UTF8 BOM
         $csvFile = str_replace("\xEF\xBB\xBF",'',$csvFile); 
         $rawData = [];
         foreach ($csvFile as $line) {
            $rawData[] = str_getcsv($line);
         }

         $data = [];
         foreach ($rawData as $key => $row) {
            //recuperer le nom des colonnes.
            $keys = explode(';',$rawData[0][0]);

            //recuperer les valeurs de la ligne courante
            $thisRawRow = explode(';',$row[0]);
            $thisRow = [];            

            // remplir le tableau cle -> valeur
            for($i = 0; $i < count($keys); $i++){
              $thisRow[$keys[$i]] = $thisRawRow[$i];
            }

            //ajouter a l ensemble des donnees.
            $data[] = $thisRow;
         }

          // verifier si les colonnes sont correctes.
          $error = "";
          
          $kk = $data[0];
          
          for($i = 1; $i <= 4; $i++){
              $col = constant("colonne_".$i);

              if(!array_key_exists($col, $data[0])) {
                $error = $error."La colonne ".$col." n'existe pas.<br>";
              }
          }

          if($error != ""){
            echo "Erreur: <br>";
            echo $error;
            exit();
          } else{ 
            copy("./tasks.csv","./tasks".time().".csv");
            move_uploaded_file($file_tmp,"./tasks.csv");
         
            echo "Les tâches ont été correctement importés! :)";
          }

      }else{
         print_r($errors);
      }
   }
?>


<html>
<head>
<style>
  img.center {
      display: block;
      margin-left: auto;
      margin-right: auto;
    width:200px;
  }
</style>
</head>
   <body style="background-color:#FFE0B2;">

     <img src="logo_nobg.png" alt="logo" class="center"> 
     Import des tâches: <br>

      <form action="" method="POST" enctype="multipart/form-data">
         <input type="file" name="csv" /> <br><br>
         <input type="submit"/>
      </form>
   </body>
</html>