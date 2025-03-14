# Tema0 POO - GwentStone Lite

În cadrul acestei teme, am avut de implementat o variantă mai simplificată a
unui joc de cărți, utilizând noțiuni elementare de programare orientată pe
obiect și limbajul Java. Plecând de la scheletul de cod oferit, am analizat
enunțul și am stabilit clasele și metodele necesare pentru implementarea
temei. Astfel, am definit pachetul "playinggame" care conține clasele ce se
ocupă de desfășurarea jocului: "Constants", "InitGame", "Player", "RunCommand"
"ShuffleCards" și ”TableCard”.

### Modificări schelet
Pentru a putea adapta codul la nevoile mele, am decis să adaug în clasele 
”CardInput” și ”DecksInput” câte un constructor care să copieze elementele
dintr-un obiect în altul, util în momentul în care, la fiecare joc, deck-urile
inițiale ale jucătorului trebuie să fie aduse la stadiul inițial. De asemenea,
în clasa ”Main” am inițiat sesiunea de joc (însemnând că declar tabla de joc
și setez numărul de câștiguri ale fiecărui player la 0), precum și fiecare 
joc în parte, păstrând în avans deck-urile jucătorilor pentru a le aduce la
starea inițială.

## Clase adăugate:

### Constants
Această clasă conține declarări de constante utilizate în tot proiectul. Am 
ales să definesc aici tipurile de comenzi pe care AI-ul le poate da, numărul 
maxim de linii și de coloane ale tablei de joc, precum și numărul maxim până
la care mana ce se adaugă crește. Am definit, de asemenea, unele tipuri de 
erori și am făcut legătura între ”front row” și ”back row” pentru fiecare 
jucător, salvând index-ul corespunzător în câte o variabilă. În final, pentru a
putea executa comenzile corespunzător, am ales să definesc mai multe liste de
string-uri pentru a ști cărțile care trebuie să stea în față, în spate, ce 
minioni sunt de tip tank, precum și câte o listă cu eroi sortați în funcție
de jucătorul asupra căruia se aplică abilitatea eroului. 

### ShuffleCards
Cu scopul de a avea un cod cât mai bine fragmentat, am ales să implementez
într-o clasă separată funcția de amestecare a cărților. Metoda definită primeș-
te deck-urile celor doi playeri și un seed, iar cu ajutorul funcției Random se
definește o ordine ”aleatoare” a cărților.

### TableCard
Pentru a putea păstra toate caracteristicile de care are nevoie o carte pe 
tabla de joc, am implementat și această clasă care, alături de parametrii unei
cărți de joc, ne poate spune și dacă, în timpul jocului, cartea a fost îngheța-
tă sau a fost deja utilizată.

### Player
Această clasă modelează jucătorul. Aceasta conține atribute și metode pentru a
gestiona starea și acțiunile playerilor pe parcursul jocului.

Atributele jucătorilor:

* hand: Reprezintă mâna curentă a jucătorului
* finishedTurn: Indicator care arată dacă jucătorul și-a terminat tura.
* mana: Reprezintă mana curentă a jucătorului.
* playerDeck: Pachetul de cărți al jucătorului.
* hero: Eroul jucătorului.
* usedHero: Indicator care arată dacă eroul a fost utilizat în runda curentă.

### InitGame & RunCommand
Clasa ”InitGame” este o componentă esențială a sistemului, responsabilă pentru 
inițializarea și gestionarea logicii jocului. Aceasta definește metode și 
procese pentru configurarea jocului, manipularea stării acestuia și executarea
comenzilor. Clasa ”RunCommand” conține implementările efective ale metodelor 
apelate de InitGame, pentru un aspect mai îngrijit al codului și pentru simpli-
ficarea mentenanței acestui cod.

Metoda statică init este punctul central al clasei și gestionează următoarele 
tipuri de funcții:

* Inițializarea jocului: Setează pachetele de cărți (decks) pentru fiecare 
jucător, configurează eroii fiecăruia, inclusiv sănătatea inițială a 
acestora, amestecă pachetele de cărți și pregătește mâinile fiecărui jucător,
respectiv setează mana inițială.

* Executarea comenzilor:
    - Comenzi de vizualizare a stadiului jocului: afișarea deck-ului unui 
    jucător, a eroului sau a manei din momentul curent, cărțile din mână, de pe
    tablă, sau cele înghețate, precum și a cărții ce ocupă o poziție dată, dacă
    există, a jocurilor câștigate de fiecare player în parte, precum și totalul
    de jocuri derulate.
    - Interacțiunea cu jocul: poziționarea unei cărți pe tabla de joc, atacarea
    unei cărți adverse, utilizarea abilității unei cărți, atacarea eroului și 
    utilizarea abilității speciale a unui erou.
    - Gestionarea turelor: afișarea jucătorului curent și încheierea turei unui
    jucător.
    
* Salvarea ieșirilor:
Output fiecărei comenzi este salvat într-un obiect ArrayNode, care se adaugă la
fișierul ce conține rezultatul final.