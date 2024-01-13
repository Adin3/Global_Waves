# Proiect GlobalWaves

<div align="center"><img src="https://i.pinimg.com/originals/d9/4e/bc/d94ebc5482cb51814420f5ba3f076020.gif" height="100px" width="1100px"></div>

## Scopul Proiectului
De a implementa functionalitatile unei aplicatii de tip audio streaming (ie Spotify, Youtube Music, SoundCloud, etc)

### Structura
La baza programului stau mai multe componente principale
* [Searchbar si Player](#searchbar-si-Player)
* [Manager](#manager)
* [User Control Flow](#user-control-flow)
* [Time Updater](#time-updater)
* [User](#user)
* [Pagination](#pagination)
* [Wrapped](#wrapped)
* [Monetisation](#monetisation)
* [Notifications](#notifications)
* [Command List](#command-list)

### Searchbar si Player
O componenta foarte importanta a programului

Fiecare user normal are un singur searchbar si player activ

daca se solicita o cautare noua, se elimina playerul curent

prin ***Design Pattern-ul Factory*** se decide ce fel de searchbar respectiv player se va folosi la runtime

![img_schema_1](https://i.imgur.com/seHoe5A.png)

in setSearchbar se instentizeaza un obiect de tip SearchbarSong, SearchbarPlaylist, SearchbarPodcast, SearchbarAlbum, respectiv SearchbarUser in functie de tipul primit la input
Cele trei clase de search mostenesc clasa mama Search care contine metodele search si select

in setMusicPlayer se instentizeaza un obiect de tip Musicplayer, PlaylistPlayer, AlbumPlayer respectiv PodcastPlayer in functie de tipul primit la input
Cele trei clase player mostenesc clasa mama Player care contine metodele load, repeat, pause, status, updateDuration, etc

### Manager

Clasa Manager este un Utility Class care se ocupa de administrarea programului

Contine multiple metode ce fac legaturi intre clase sau a metode care nu se potriveau unei clase specifice, 
si campuri utile precum *users* care asigura maparea dintre username si user pentru accesarea informatilor mai usor
sau *playlists* care precum *users* asigura maparea nume playlist - playlist

### User Control Flow

Tot in clasa Manager se poate gasi metoda checkSource care verifica control flowul userului in functie de ce comenzi sau apelat

Se comporta asemenea unui state machine

metoda se foloseste de un obiect de tip Source care contine trei flaguri
-SourceSearched
-SourceSelected
-SourceLoaded

SourceSearched este declansat dupa o cautare ce a avut succes
daca se activeaza SourceSearched, SourceLoaded se opreste

SourceSelected este declansat dupa o selectare corecta si daca SourceSearched este activ
daca se activeaza SourceSelected, SourceSearched se opreste

SourceLoaded este declansat dupa ce o sursa a fost incarcata si daca SourceSelected e activ
daca se activeaza SourceLoaded, SourceSelected se opreste

cu ajutorul acestor flaguri se poate verifica la runtime daca utilizatorul are voie sa foloseasca comanda respectiva la momentul acela de timp

### Time Updater

se calculeaza in main variabila deltaTime care reprezinta variatia de timp dintre doua comenzi apelate (efectiv Δt)

se paseaza deltaTime in updatePlayers care updateaza fiecare player activ

Daca mai rămâne timp după ce se scade toata durata din melodie, se trece la următoarea melodie după parametrii player-ului (repeat shuffle)

Daca inca mai rămâne timp se repeta pașii

### User

Programul tine cont de faptul ca pot exista diferite tipuri de useri

Userii pot fi de 3 tipuri: 
* Normal
* Artist
* Host

#### Normal User

Normal userii sunt cei ce beneficiaza de majoritatea functionalitatilor
din program

Au un singur player si searchbar care le permit sa folosească majoritatea comenzile
principale ale programului.

Pe langa toate aceste comenzi, useri normali pot sa isi schimbe starea contului
in online sau offline

In timp ce sunt offline, nu au acces la o parte buna din comenzi, iar playerul
lor este pus pe pauza pana se conecteaza inapoi si devin online

Userii normali mai au si posibilitatea de a accesa diferite pagini din
program cum ar fi pagina principala cu recomandari globale, pagina de preferinte, si
pagina unui artist sau a unui host de podcast

In momentul in care decide userul sa isi stearga contul, playlisturile
acestuia nu trebuie sa fie accesate de nimeni din platforma pentru a se
aplica stergerea contului. Dupa stergere toti cei ce dadeau follow la unul
dintre playlisturile contului sterg o sa isi piarda followul, si toate
like-urile si follow-urile contului sterg nu vor mai fi considerate

Comenzii specifice Normal user: search, select, load, status, playPause,
createPlaylist, addRemoveInPlaylist, like, showPlaylists, showPreferredSongs,
repeat, shuffle, next, prev, forward, backward, follow, switchVisibility,
switchConnectionStatus, printCurrentPage, changePage, removeUser

#### Artist User

Artistii pot sa creeze albume noi si sa isi customizeze pagina sa

Dupa ce un artist a creat un album, album si melodiile sale vor fi salvate
in Singleton-ul Library pentru mai apoi sa fie accesate de useri normali

Ca artist, poti sa iti modifici pagina pe care o pot accesa alti useri, prin
adaugarea de evenimente sau de merch

Daca un artist isi doreste sa stearga un album, albumul acestuia nu trebuie sa fie
accesat de nimeni din platforma pentru a se aplica stergerea albumului.

In momentul in care decide artistul sa isi stearga contul, albumele si pagina sa
nu trebuie sa fie accesata de nimeni din platforma. Dupa ce contul a fost sters
melodiile si albumele sale vor fi eliminate din Library

Comenzi specifice Artist User: addAlbum, showAlbums, addEvent, addMerch, deleteUser,
removeAlbum, removeEvent

#### Host User

hosti pot sa creeze podcast-uri noi si sa isi customizeze pagina sa

Dupa ce un host a creat un podcast, podcastul lui va fi salvat
in Singleton-ul Library pentru mai apoi a fi accesat de useri normali

Ca host, poti sa iti modifici pagina pe care o pot accesa alti useri, prin
adaugarea de anunturi

Daca un host isi doreste sa stearga un podcast, podcastul acestuia nu trebuie sa fie
accesat de nimeni din platforma pentru a se aplica stergerea podcastului.

In momentul in care decide hostul sa isi stearga contul, podcasturile si pagina sa
nu trebuie sa fie accesata de nimeni din platforma. Dupa ce contul a fost sters
podcasturile hostului vor fi eliminate din Library

Comenzi specifice Host User: addPodcast, addAnnouncement, removeAnnouncement,
showPodcasts, removePodcast, deleteUser

Mai exista si comenzi pur statistice la care acces doar adminul programului: getTop5Playlists,
getTop5Songs, getOnlineUsers, getAllUsers, getTop5Albums, getTop5Artists

### Pagination

Programul le permite utilizatorilor accesul la un sistem de pagini

Utilizatori normali pot accesa: HomePage, Liked Content, Artist's Page, Host's Page
iar artistii, si hostii pot doar sa isi modifice pagina personala.

un user normal poate sa acceseze pagina unui artist sau host prin cautarea si selectarea
sa din Library sau prin folosirea comenzii de changePage Host/Artist care va selecta pagina
artistului/hostului al carui fisier este in player-ul userului normal

Userii normali mai pot sa se intoarca inapoi la paginile anterioare, istoricul de paginii
fiind salvat

In acelasi timp, un user normal de pe platforma poate primii recomandari noi de tip
melodie sau playlist ce vor fi salvate pe HomePage-ul sa, si la care mai apoi sa dea load
in player

Tot sistemul de paginatie este gandit cu ***Design Pattern-ul Strategy***

![img_schema_1](https://i.imgur.com/5IqxTQ1.png)

### Wrapped
wrapped reprezinta statistice personalizate ale unui user

wrapped este diferit pentru fiecare tip de user 
si tine cont de melodiile si episoade ascultate pe platforma

la apelarea comenzi *wrapped* se vor afisa top5-ul pentru fiecare categorie
importanta pentru tipul userului respectiv in functie de listenuri

### Monetisation
artistii vor fii platiti pentru ascultarile pe melodii lor

un user normal poate sa cumpere o subscriptie la 1000000 de credite pentru premium

Userii premium vor putea platii egal melodiile ascultate de acestia prin aceasta formula

 	Revenue = premium_price/total_listened_song * nr_of_songs_from_artist

Userii non-premium vor primi ocazional ad-break-uri. Dupa ce s-a terminat ad-ul,
artistii vor primii pe melodiile ascultate de user intre ad-breakuri dupa formula aceasta

    Revenue = ad_price/total_listened_song_between_ads * nr_of_songs_from_artist

Useri normali mai pot sa cumpere merch de pe pagina artisului, acesta fiind inca un mod prin
care artistul poate sa obtina bani

la sfarsitul programului se afiseaza comanda *endProgram* 
care arata statistici monetare despre toti artisti activi.
(artist ce a facut fie venit, fie a fost ascultat de un user normal)

### Notifications
Orice user normal poate sa de-a subscribe la un artist/host de pe pagina acestuia.
Odata ce a dat subscribe, userul respectiv devine un subscriber al acelui artist/host

In cazul in care artistul creaza un event, album, sau merch nou;
toti subscriberi acestuia vor fii notificati

In cazul in care hostul creaza un announcement nou,
toti subscriberi acestuia vor fii notificati

Tot sistemul de paginatie este gandit cu ***Design Pattern-ul Observer***

### Command List

Este un utility class folosit pe post de wrapper pentru comenzile programului

In fiecare metoda wrapper se aplica metoda prorpiu zisa, dar si ceva logica inainte de aceasta
(De exemplu verificarea userului)

Design Pattern folosit: Singleton(Library), Factory(Searchbar, Player), Strategy(Page), Observer(User), Builder(Event)

![img_schema_1](https://tenor.com/view/frustrated-mad-fuck-this-homework-work-gif-15260947.gif)
