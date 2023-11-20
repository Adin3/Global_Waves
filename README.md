# Proiect GlobalWaves  - Etapa 1

<div align="center"><img src="https://i.pinimg.com/originals/d9/4e/bc/d94ebc5482cb51814420f5ba3f076020.gif" height="100px" width="1100px"></div>

## Scopul Proiectului
De a implementa functionalitatile unei aplicatii de tip audio streaming (ie Spotify, Youtube Music, SoundCloud, etc)

## Structura
La baza programului stau 4 componente principale
* Searchbar si Player
* Manager
* User Control Flow
* Time Updater

### Searchbar si Player
O componenta foarte importanta a programului

Fiecare user are un singur searchbar si player activ

daca se solicita o cautare noua, se elimina playerul curent

prin polymorfism dinamic si campul type din user se decide ce fel de searchbar respectiv player se va folosi la runtime

![img_schema_1](https://i.imgur.com/cN3fC0K.png)

in setSearchbar se instentizeaza un obiect de tip SearchbarSong, SearchbarPlaylist respectiv SearchbarPodcast in functie de tipul primit la input
Cele trei clase de search mostenesc clasa mama Search care contine metodele search si select

in setMusicPlayer se instentizeaza un obiect de tip Musicplayer, PlaylistPlayer respectiv PodcastPlayer in functie de tipul primit la input
Cele trei clase player mostenesc clasa mama Player care contine metodele load, repeat, pause, status, updateDuration, etc

### Manager

Clasa Manager este un Utility Class care se ocupa de administrarea programului

Contine multiple metode ce fac legaturi intre clase sau a metode care nu se potriveau unei clase specifice, 
si campuri utile precum *users* care asigura maparea dintre username si user pentru accesarea informatilor mai usor

### User Control Flow

Tot in clasa Manager se poate gasi metoda checkSource care verifica control flowul userului in functie de ce comenzi sau apelat

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

<div align="center"><img src="https://tenor.com/view/frustrated-mad-fuck-this-homework-work-gif-15260947.gif" width="500px"></div>
