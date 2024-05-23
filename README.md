# OuisticiApp

Ouistici est une application mobile android permettant de configurer à distance des balises sonores. Ces balises peuvent êtres placées à différents types d'emplacements (Lieux publiques, particuliers, ...) et représentent une version peu onéreuse des balises que l'on connaît actuellement. Grâce à l'application, nous pouvons choisir les messages souhaités que la balise dira une fois la télécommande actionnée. On peut choisir un message par défaut mais aussi personnalisé les messages en fonction de plages horaires (Exemple : du lundi au vendredi de 8h à 12h la balise dira "Bienvenue à l'université de ..., notre accueil est ouvert est disponible sur votre droite ...").

# Sommaire

[Diagramme de classe](#classe)


# Diagramme de classe <a id="classe"></a>
```plantuml
@startuml
hide circle
allowmixing
skinparam classAttributeIconSize 0
skinparam classBackgroundColor #ffffb9
skinparam classBorderColor #800000
skinparam classArrowColor #800000
skinparam classFontColor #black
skinparam classFontName Tahoma


class AndroidAudioPlayer {
    - context: Context
    - player: MediaPlayer?=null
    + playFile(file: File): Void
    + stop(): Void
    + getAudioDuration(file: File): Int
}


class AndroidAudioRecorder {
    - context: Context
    - recorder: MediaRecorder?=null
    + createRecorder(): MediaRecorder
    + start(outputFile: File): Void
    + stop(): Void
}


class Annonce {
    + id: Int
    + nom: String
    + type: TypeAnnonce
    + audio: File?
    + contenu : String?
    + langue: Langue?
    + duree: Int?
    + filename: String
}


interface AudioPlayer {
    + playFile(file: File): Void
    + stop(): Void
}


interface AudioRecorder {
    + start(outputFile: File): Void
    + stop(): Void
}


class Balise {
    + nom: String
    + lieu: String
    + defaultMessage: Annonce?
    + annonces: ArrayList<Annonce>
    + volume: Float
    + plages: ArrayList<PlageHoraire>
    + sysOnOff: Boolean
    + ipBal: String
    + createId(): Int
}


enum JoursSemaine {
    Lundi
    Mardi
    Mercredi
    Jeudi
    Vendredi
    Samedi
    Dimanche
}


class Langue {
    + code: String
    + nom: String
    + getLangueName(): String
}


class LangueManager {
    + languesDisponibles: ListOf<Langue>
    + langueActuelle: MutableStateOf<Langue>
}


class PlageHoraire {
    + nomMessage: Annonce
    + jours: List<JoursSemaine>
    + heureDebut: LocalTime
    + heureFin: LocalTime
}


class TextToSpeechManager {
    + context: Context
    - tts: TextToSpeech
    - isInitialized: Boolean=false
    + onInit(status: Int): Void
    + setLanguage(locale: Locale): Void
    + saveToFile(text: String, file: File): Void
    + shutdown(): Void
}


enum TypeAnnonce {
    AUDIO
    TEXTE
}


class RestApiService {
    + setVolume(baliseData: BaliseDto, onResult: (BaliseDto?)->Unit): Void
    + setNameAndPlace(baliseData: BaliseDto, onResult: (BaliseDto?)->Unit): Void
    + testSound(callback: (Int)->Unit): Void
    + setDefaultMessage(baliseData: BaliseDto, onResult: (BaliseDto?)->Unit): Void
    + createAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?)->Unit): Void
    + modifyAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?)->Unit): Void
    + deleteAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?)->Unit): Void
    + createAudio(fileAnnonceData: FileAnnonceDto, onResult: (FileAnnonceDto?)->Unit): Void
}


class FileAnnonceDto {
    + code: Int?
    + value: String
    + audioFile: File
}


class BaliseDto {
    + balId: Int?
    + nom: String?
    + lieu: String
    + defaultMessage: Int?
    + volume: Float
    + sysOnOff: Boolean
    + ipBal: String
}


class AnnonceDto {
    + upload_sound_url: String?
    + id_annonce: Int
    + nom: String
    + type: String
    + contenu: String?
    + langue: String?
    + duree: Int?
    + filename: String?
}


class OuisticiApi {
    + setVolume(volume: JsonObject): Call<BaliseDto>
    + setNameAndPlace(nameAndPlace: JsonObject): Call<BaliseDto>
    + testSound(): Call<Void>
    + setDefaultMessage(defaultMessage: JsonObject): Call<BaliseDto>
    + createAnnonce(annonce: JsonObject): Call<AnnonceDto>
    + modifyAnnonce(annonce: JsonObject): Call<AnnonceDto>
    + deleteAnnonce(annonce: JsonObject): Call<AnnonceDto>
    + createAudio(description: RequestBody, audioFile: MultipartBody.Part): Call<FileAnnonceDto>
}


class BaliseViewModel {
    + selectedBalise: Balise?=null
}


object RetrofitClient {
    - client: OkHttpClient!
    - retrofit: Retrofit!
    + buildService(service: Class<T>): T
}


class MainActivity {
    - recorder: AndroidAudioRecorder
    - player: AndroidAudioPlayer
    + onCreate(savedInstanceBundle: Bundle?): Void
}


@enduml
```