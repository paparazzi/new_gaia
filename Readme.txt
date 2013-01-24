###########################################################
#                                                         #
#         Simulateur d'environnement pour drones          #
#                                                         #
###########################################################

Groupe : Ons BENREJEB, Sébastien CARRIERE, Alexandra OTT, Hugo EBRO


Compilation :

* Décompressez le contenu de l'archive .zip,
* Exécutez la commande   make

Exécution :

* Pour pouvoir utiliser le simulateur dans la totalité de ses fonctionnalités,
le logiciel Paparazzi est souhaitable :
http://paparazzi.enac.fr/wiki/Main_Page
* Exécutez la commande   make run

Installation de Paparazzi :

* Pour comprendre les commandes ci-dessous, ou pour une installation différente, nous vous redirigeons vers les pages web consacrées :
instructions communes : http://paparazzi.enac.fr/wiki/Installation#OS_Specific_Instructions
Linux : http://paparazzi.enac.fr/wiki/Installation/Linux
Mac OS X : http://paparazzi.enac.fr/wiki/Installation/MacOSX
* Commandes correctes pour une version ubuntu :

	1) sudo add-apt-repository ppa:paparazzi-uav/ppa
        2) sudo apt-get update
        3) sudo apt-get install paparazzi-dev paparazzi-arm-multilib
--> prendre soin de se placer dans le répertoire où on souhaite voir le dossier paparazzi installé.
        4) git clone https://github.com/paparazzi/paparazzi.git
        5) cd paparazzi
        6) make
Vous pouvez alors lancer paparazzi : ./paparazzi .
Si vous avez des problèmes de droits, changez le propriétaire : chown -R (user) ./ (si ça persiste...vous pouvez exécuter en tant qu'administrateur..)
Une fois paparazzi lancé, sélectionnez un drone : Twinjet par exemple, sélectionnez comme "target" sim et faites un build.
Il ne reste plus qu'à appuyer sur "Execute"

Avec Paparazzi :

* Veillez à décompresser le contenu de l'archive dans un dossier adjacent au dossier   paparazzi,
* Lancez une session de simulation Paparazzi (traget sim, A/C Microjet par exemple, Build puis Execute),
* Lancez le drone (Take Off puis Launch),
* Enjoy !

Cartes SRTM :

* Pour que le relif soit pris en compte, il est nécessaire de charger les cartes SRTM appropriées
à la simulation en cours,
* Pour ce faire, veuillez les télécharger sur le site   http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/
* Une fois téléchargées, les cartes SRTM doivent être placées dans le dossier   paparazzi/data/srtm/

Google Maps :

* Pour que les cartes Google Maps s'affichent correctement, les dalles Google Maps doivent d'abord être chargées
via Paparazzi (icône Google Earth, petite planète bleue).


         _  _
        ( `   )_
       (    )    `)
     (_   (_ .  _) _)
                                    _
                                   (  )
    _ .                         ( `  ) . )
  (  _ )_                      (_, _(  ,_)_)
(_  _(_ ,)
                                                       |
         _  _                                        \ _ /
        ( `   )_                                   -= (_) =-
       (    )    `)                                  /   \
     (_   (_ .  _) _)                                  |

                                _
                              -=\`\
                          |\ ____\_\__
                        -=\c`""""""" "`)
                           `~~~~~/ /~~`
                             -==/ /
                               '-'                 _
                                                  (  )
                _, _ .                         ( `  ) . )
               ( (  _ )_                      (_, _(  ,_)_)
             (_(_  _(_ ,)

