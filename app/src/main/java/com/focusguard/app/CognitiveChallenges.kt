package com.focusguard.app

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.components.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// ========== QUIZ DE CULTURE GÉNÉRALE ==========

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

val quizQuestions = listOf(
    QuizQuestion("Quelle est la capitale du Japon ?", listOf("Tokyo", "Kyoto", "Osaka", "Hiroshima"), "Tokyo"),
    QuizQuestion("Qui a peint La Joconde ?", listOf("Léonard de Vinci", "Michel-Ange", "Raphaël", "Donatello"), "Léonard de Vinci"),
    QuizQuestion("Combien de continents y a-t-il sur Terre ?", listOf("5", "6", "7", "8"), "7"),
    QuizQuestion("Quelle est la planète la plus proche du Soleil ?", listOf("Vénus", "Mercure", "Mars", "Terre"), "Mercure"),
    QuizQuestion("En quelle année l'homme a-t-il marché sur la Lune ?", listOf("1965", "1969", "1972", "1975"), "1969"),
    QuizQuestion("Quel est le plus grand océan du monde ?", listOf("Atlantique", "Indien", "Pacifique", "Arctique"), "Pacifique"),
    QuizQuestion("Qui a écrit 'Les Misérables' ?", listOf("Victor Hugo", "Émile Zola", "Balzac", "Flaubert"), "Victor Hugo"),
    QuizQuestion("Combien de joueurs dans une équipe de football ?", listOf("9", "10", "11", "12"), "11"),
    QuizQuestion("Quel est le symbole chimique de l'or ?", listOf("Or", "Au", "Ag", "Go"), "Au"),
    QuizQuestion("Quelle est la capitale de l'Australie ?", listOf("Sydney", "Melbourne", "Canberra", "Brisbane"), "Canberra"),
    QuizQuestion("Qui a peint 'La Joconde' ?", listOf("Michel-Ange", "Léonard de Vinci", "Raphaël", "Donatello"), "Léonard de Vinci"),
    QuizQuestion("En quelle année a eu lieu la Révolution française ?", listOf("1789", "1799", "1804", "1815"), "1789"),
    QuizQuestion("Quel est l'élément chimique de symbole 'Au' ?", listOf("Argent", "Or", "Aluminium", "Arsenic"), "Or"),
    QuizQuestion("Combien y a-t-il de continents sur Terre ?", listOf("5", "6", "7", "8"), "7"),
    QuizQuestion("Qui a écrit 'Les Misérables' ?", listOf("Victor Hugo", "Émile Zola", "Alexandre Dumas", "Gustave Flaubert"), "Victor Hugo"),
    QuizQuestion("Quelle est la planète la plus proche du Soleil ?", listOf("Vénus", "Mars", "Mercure", "Terre"), "Mercure"),
    QuizQuestion("En quelle année l'homme a-t-il marché sur la Lune pour la première fois ?", listOf("1965", "1967", "1969", "1971"), "1969"),
    QuizQuestion("Quel océan est le plus grand ?", listOf("Atlantique", "Indien", "Arctique", "Pacifique"), "Pacifique"),
    QuizQuestion("Qui a composé 'La 9ème Symphonie' ?", listOf("Mozart", "Beethoven", "Bach", "Vivaldi"), "Beethoven"),
    QuizQuestion("Quelle est la langue la plus parlée au monde ?", listOf("Anglais", "Espagnol", "Mandarin", "Hindi"), "Mandarin"),
    QuizQuestion("Combien de touches a un piano standard ?", listOf("76", "88", "92", "100"), "88"),
    QuizQuestion("Quel pays a remporté le plus de Coupes du Monde de football ?", listOf("Allemagne", "Argentine", "Brésil", "Italie"), "Brésil"),
    QuizQuestion("Quelle est la montagne la plus haute du monde ?", listOf("K2", "Mont Everest", "Kilimandjaro", "Mont Blanc"), "Mont Everest"),
    QuizQuestion("Qui a découvert la pénicilline ?", listOf("Louis Pasteur", "Alexander Fleming", "Marie Curie", "Albert Einstein"), "Alexander Fleming"),
    QuizQuestion("Quelle est la capitale du Japon ?", listOf("Osaka", "Kyoto", "Tokyo", "Hiroshima"), "Tokyo"),
    QuizQuestion("Combien de côtés a un hexagone ?", listOf("5", "6", "7", "8"), "6"),
    QuizQuestion("Qui a écrit 'Roméo et Juliette' ?", listOf("Charles Dickens", "William Shakespeare", "Oscar Wilde", "Jane Austen"), "William Shakespeare"),
    QuizQuestion("Quel est le symbole chimique du fer ?", listOf("F", "Fe", "Fr", "Fm"), "Fe"),
    QuizQuestion("En quelle année a débuté la Première Guerre mondiale ?", listOf("1912", "1914", "1916", "1918"), "1914"),
    QuizQuestion("Quelle est la plus grande île du monde ?", listOf("Madagascar", "Bornéo", "Groenland", "Nouvelle-Guinée"), "Groenland"),
    QuizQuestion("Qui a peint 'La Nuit étoilée' ?", listOf("Pablo Picasso", "Claude Monet", "Vincent van Gogh", "Salvador Dalí"), "Vincent van Gogh"),
    QuizQuestion("Combien de joueurs y a-t-il dans une équipe de football ?", listOf("9", "10", "11", "12"), "11"),
    QuizQuestion("Quelle est la vitesse de la lumière ?", listOf("300 000 km/s", "150 000 km/s", "450 000 km/s", "600 000 km/s"), "300 000 km/s"),
    QuizQuestion("Qui a développé la théorie de la relativité ?", listOf("Isaac Newton", "Galilée", "Albert Einstein", "Stephen Hawking"), "Albert Einstein"),
    QuizQuestion("Quelle est la capitale de l'Espagne ?", listOf("Barcelone", "Madrid", "Séville", "Valence"), "Madrid"),
    QuizQuestion("Combien de dents a un adulte humain ?", listOf("28", "30", "32", "34"), "32"),
    QuizQuestion("Quel fleuve traverse Paris ?", listOf("La Loire", "La Seine", "Le Rhône", "La Garonne"), "La Seine"),
    QuizQuestion("Qui a écrit '1984' ?", listOf("Aldous Huxley", "Ray Bradbury", "George Orwell", "H.G. Wells"), "George Orwell"),
    QuizQuestion("Quelle est la plus petite planète du système solaire ?", listOf("Mars", "Mercure", "Pluton", "Vénus"), "Mercure"),
    QuizQuestion("En quelle année la Seconde Guerre mondiale s'est-elle terminée ?", listOf("1943", "1944", "1945", "1946"), "1945"),
    QuizQuestion("Quel est le plus long fleuve du monde ?", listOf("Amazone", "Nil", "Yangtsé", "Mississippi"), "Nil"),
    QuizQuestion("Qui a inventé le téléphone ?", listOf("Thomas Edison", "Nikola Tesla", "Alexander Graham Bell", "Guglielmo Marconi"), "Alexander Graham Bell"),
    QuizQuestion("Quelle est la capitale de l'Italie ?", listOf("Milan", "Venise", "Florence", "Rome"), "Rome"),
    QuizQuestion("Combien de cordes a une guitare classique ?", listOf("4", "5", "6", "7"), "6"),
    QuizQuestion("Qui a écrit 'Le Petit Prince' ?", listOf("Jules Verne", "Antoine de Saint-Exupéry", "Marcel Pagnol", "Albert Camus"), "Antoine de Saint-Exupéry"),
    QuizQuestion("Quel est le symbole chimique de l'oxygène ?", listOf("O", "Ox", "Og", "Om"), "O"),
    QuizQuestion("Quelle planète est surnommée la 'planète rouge' ?", listOf("Vénus", "Mars", "Jupiter", "Saturne"), "Mars"),
    QuizQuestion("En quelle année a été créé Facebook ?", listOf("2002", "2004", "2006", "2008"), "2004"),
    QuizQuestion("Quelle est la monnaie du Japon ?", listOf("Yuan", "Won", "Yen", "Baht"), "Yen"),
    QuizQuestion("Qui a peint 'Guernica' ?", listOf("Salvador Dalí", "Pablo Picasso", "Joan Miró", "Francisco Goya"), "Pablo Picasso"),
    QuizQuestion("Combien de secondes y a-t-il dans une heure ?", listOf("3000", "3200", "3400", "3600"), "3600"),
    QuizQuestion("Quelle est la capitale du Canada ?", listOf("Toronto", "Montréal", "Vancouver", "Ottawa"), "Ottawa"),
    QuizQuestion("Qui a découvert l'Amérique en 1492 ?", listOf("Amerigo Vespucci", "Christophe Colomb", "Vasco de Gama", "Ferdinand Magellan"), "Christophe Colomb"),
    QuizQuestion("Quelle est la formule chimique de l'eau ?", listOf("H2O", "CO2", "O2", "H2SO4"), "H2O"),
    QuizQuestion("En quelle année le Titanic a-t-il coulé ?", listOf("1910", "1912", "1914", "1916"), "1912"),
    QuizQuestion("Quelle est la plus grande ville du monde par population ?", listOf("Shanghai", "Tokyo", "Delhi", "São Paulo"), "Tokyo"),
    QuizQuestion("Qui a écrit 'L'Odyssée' ?", listOf("Homère", "Virgile", "Sophocle", "Euripide"), "Homère"),
    QuizQuestion("Combien de faces a un cube ?", listOf("4", "6", "8", "12"), "6"),
    QuizQuestion("Quelle est la langue officielle du Brésil ?", listOf("Espagnol", "Portugais", "Français", "Italien"), "Portugais"),
    QuizQuestion("Qui a composé 'Les Quatre Saisons' ?", listOf("Bach", "Mozart", "Vivaldi", "Haendel"), "Vivaldi"),
    QuizQuestion("Quel est le plus petit pays du monde ?", listOf("Monaco", "Vatican", "Saint-Marin", "Liechtenstein"), "Vatican"),
    QuizQuestion("En quelle année est tombé le mur de Berlin ?", listOf("1987", "1988", "1989", "1990"), "1989"),
    QuizQuestion("Quelle est la capitale de l'Égypte ?", listOf("Alexandrie", "Le Caire", "Louxor", "Gizeh"), "Le Caire"),
    QuizQuestion("Qui a inventé l'ampoule électrique ?", listOf("Nikola Tesla", "Thomas Edison", "Benjamin Franklin", "Alessandro Volta"), "Thomas Edison"),
    QuizQuestion("Combien de couleurs y a-t-il dans un arc-en-ciel ?", listOf("5", "6", "7", "8"), "7"),
    QuizQuestion("Quelle est la capitale de la Russie ?", listOf("Saint-Pétersbourg", "Moscou", "Kiev", "Minsk"), "Moscou"),
    QuizQuestion("Qui a écrit 'Le Comte de Monte-Cristo' ?", listOf("Victor Hugo", "Alexandre Dumas", "Honoré de Balzac", "Stendhal"), "Alexandre Dumas"),
    QuizQuestion("Quel est le symbole chimique du sodium ?", listOf("S", "So", "Na", "Sd"), "Na"),
    QuizQuestion("En quelle année a eu lieu la chute de Constantinople ?", listOf("1453", "1492", "1517", "1571"), "1453"),
    QuizQuestion("Quelle est le plus grand désert du monde ?", listOf("Sahara", "Gobi", "Antarctique", "Arabie"), "Antarctique"),
    QuizQuestion("Qui a peint 'La Cène' ?", listOf("Michel-Ange", "Léonard de Vinci", "Raphaël", "Le Caravage"), "Léonard de Vinci"),
    QuizQuestion("Combien de merveilles du monde antique existaient ?", listOf("5", "6", "7", "8"), "7"),
    QuizQuestion("Quelle est la capitale de l'Argentine ?", listOf("Santiago", "Montevideo", "Buenos Aires", "Lima"), "Buenos Aires"),
    QuizQuestion("Qui a écrit 'Crime et Châtiment' ?", listOf("Léon Tolstoï", "Fiodor Dostoïevski", "Anton Tchekhov", "Ivan Tourgueniev"), "Fiodor Dostoïevski"),
    QuizQuestion("Quelle est la température de congélation de l'eau ?", listOf("-5°C", "0°C", "5°C", "10°C"), "0°C"),
    QuizQuestion("En quelle année a eu lieu l'attaque de Pearl Harbor ?", listOf("1939", "1940", "1941", "1942"), "1941"),
    QuizQuestion("Quelle est la capitale de la Grèce ?", listOf("Thessalonique", "Athènes", "Sparte", "Corinthe"), "Athènes"),
    QuizQuestion("Qui a composé 'La Flûte enchantée' ?", listOf("Beethoven", "Mozart", "Haydn", "Schubert"), "Mozart"),
    QuizQuestion("Combien de planètes y a-t-il dans le système solaire ?", listOf("7", "8", "9", "10"), "8"),
    QuizQuestion("Quelle est la monnaie de la Suisse ?", listOf("Euro", "Franc suisse", "Dollar", "Couronne"), "Franc suisse"),
    QuizQuestion("Qui a peint 'Les Tournesols' ?", listOf("Claude Monet", "Vincent van Gogh", "Paul Gauguin", "Édouard Manet"), "Vincent van Gogh"),
    QuizQuestion("En quelle année a été fondée l'ONU ?", listOf("1943", "1944", "1945", "1946"), "1945"),
    QuizQuestion("Quelle est la capitale du Portugal ?", listOf("Porto", "Lisbonne", "Braga", "Coimbra"), "Lisbonne"),
    QuizQuestion("Qui a écrit 'Germinal' ?", listOf("Victor Hugo", "Émile Zola", "Guy de Maupassant", "Gustave Flaubert"), "Émile Zola"),
    QuizQuestion("Quel est le symbole chimique du carbone ?", listOf("Ca", "Cr", "C", "Co"), "C"),
    QuizQuestion("Combien de cents y a-t-il dans un dollar ?", listOf("50", "100", "200", "500"), "100"),
    QuizQuestion("Quelle est la plus haute tour du monde ?", listOf("Tour Eiffel", "Empire State Building", "Burj Khalifa", "Shanghai Tower"), "Burj Khalifa"),
    QuizQuestion("Qui a découvert la radioactivité ?", listOf("Marie Curie", "Pierre Curie", "Henri Becquerel", "Ernest Rutherford"), "Henri Becquerel"),
    QuizQuestion("En quelle année a eu lieu la Révolution russe ?", listOf("1905", "1914", "1917", "1920"), "1917"),
    QuizQuestion("Quelle est la capitale de la Norvège ?", listOf("Stockholm", "Copenhague", "Oslo", "Helsinki"), "Oslo"),
    QuizQuestion("Qui a écrit 'Don Quichotte' ?", listOf("Lope de Vega", "Miguel de Cervantes", "Calderón de la Barca", "Francisco de Quevedo"), "Miguel de Cervantes"),
    QuizQuestion("Combien de minutes y a-t-il dans une journée ?", listOf("1200", "1440", "1680", "2000"), "1440"),
    QuizQuestion("Quelle est la capitale de la Turquie ?", listOf("Istanbul", "Ankara", "Izmir", "Bursa"), "Ankara"),
    QuizQuestion("Qui a composé 'Le Lac des cygnes' ?", listOf("Tchaïkovski", "Stravinsky", "Prokofiev", "Rachmaninov"), "Tchaïkovski"),
    QuizQuestion("Quelle est la vitesse du son ?", listOf("240 m/s", "280 m/s", "340 m/s", "380 m/s"), "340 m/s"),
    QuizQuestion("En quelle année a été inventée l'imprimerie par Gutenberg ?", listOf("1400", "1440", "1450", "1480"), "1450"),
    QuizQuestion("Quelle est la capitale de la Suède ?", listOf("Oslo", "Copenhague", "Stockholm", "Helsinki"), "Stockholm"),
    QuizQuestion("Qui a écrit 'Les Trois Mousquetaires' ?", listOf("Victor Hugo", "Alexandre Dumas", "Jules Verne", "Honoré de Balzac"), "Alexandre Dumas"),
    QuizQuestion("Quel est le symbole chimique du calcium ?", listOf("C", "Cl", "Ca", "Cs"), "Ca"),
    QuizQuestion("Combien de symphonies Beethoven a-t-il composées ?", listOf("7", "8", "9", "10"), "9"),
    QuizQuestion("Quelle est la plus grande forêt tropicale du monde ?", listOf("Forêt du Congo", "Amazonie", "Forêt de Bornéo", "Forêt de Nouvelle-Guinée"), "Amazonie"),
    QuizQuestion("Qui a peint 'Le Cri' ?", listOf("Edvard Munch", "Gustav Klimt", "Egon Schiele", "Wassily Kandinsky"), "Edvard Munch"),
    QuizQuestion("En quelle année a été créé Google ?", listOf("1996", "1998", "2000", "2002"), "1998"),
    QuizQuestion("Quelle est la capitale des Pays-Bas ?", listOf("Rotterdam", "Amsterdam", "La Haye", "Utrecht"), "Amsterdam"),
    QuizQuestion("Qui a écrit 'Le Seigneur des Anneaux' ?", listOf("C.S. Lewis", "J.R.R. Tolkien", "George R.R. Martin", "Terry Pratchett"), "J.R.R. Tolkien"),
    QuizQuestion("Quelle est la température d'ébullition de l'eau ?", listOf("90°C", "95°C", "100°C", "105°C"), "100°C"),
    QuizQuestion("Combien de livres compte la Bible ?", listOf("63", "66", "69", "72"), "66"),
    QuizQuestion("Quelle est la langue officielle du Brésil ?", listOf("Espagnol", "Portugais", "Français", "Italien"), "Portugais")
)

@Composable
fun QuizChallenge(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val totalQuestions = AppPreferences.getQuizCount(context)
    val questions = remember { quizQuestions.shuffled().take(totalQuestions) }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var showFailure by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentQuestionIndex]

    LaunchedEffect(isAnswered) {
        if (isAnswered && selectedAnswer == currentQuestion.correctAnswer) {
            correctAnswers++
        }

        if (isAnswered) {
            delay(1500)
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                selectedAnswer = null
                isAnswered = false
            } else {
                // Quiz terminé - vérifier si TOUTES les réponses sont correctes
                showResult = true
                delay(2000)

                if (correctAnswers == questions.size) {
                    // Toutes les réponses sont bonnes -> débloquer
                    onComplete()
                } else {
                    // Au moins une erreur -> afficher l'échec
                    showFailure = true
                }
            }
        }
    }

    // Dialog d'échec
    if (showFailure) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Échec") },
            text = {
                Text("Tu dois répondre correctement à toutes les questions !\n\nScore : $correctAnswers/${questions.size}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Recommencer le quiz
                        currentQuestionIndex = 0
                        correctAnswers = 0
                        selectedAnswer = null
                        isAnswered = false
                        showResult = false
                        showFailure = false
                    }
                ) {
                    Text("Recommencer")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header with progress
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AppColors.Info,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Quiz Culture G",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = "Question ${currentQuestionIndex + 1}/$totalQuestions",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }

        if (!showResult) {
            // Question Card — glassmorphism
            GlassCard(accentColor = AppColors.Info) {
                Text(
                    text = currentQuestion.question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Options
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentQuestion.options.forEach { option ->
                    val isThisSelected = selectedAnswer == option
                    val backgroundColor = when {
                        !isAnswered -> if (isThisSelected) AppColors.GlassBgElevated else AppColors.GlassBg
                        option == currentQuestion.correctAnswer -> AppColors.Success.copy(alpha = 0.2f)
                        isThisSelected -> AppColors.Error.copy(alpha = 0.2f)
                        else -> AppColors.GlassBg
                    }
                    val borderColor = when {
                        !isAnswered -> if (isThisSelected) AppColors.Primary.copy(alpha = 0.4f) else AppColors.GlassBorderLight
                        option == currentQuestion.correctAnswer -> AppColors.Success.copy(alpha = 0.5f)
                        isThisSelected -> AppColors.Error.copy(alpha = 0.5f)
                        else -> AppColors.GlassBorderLight
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedAnswer = option
                            }
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                fontWeight = if (isThisSelected) FontWeight.Bold else FontWeight.Normal,
                                color = AppColors.OnSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswered) {
                                Icon(
                                    imageVector = if (option == currentQuestion.correctAnswer)
                                        Icons.Filled.Check
                                    else if (isThisSelected)
                                        Icons.Filled.Close
                                    else
                                        Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = when {
                                        option == currentQuestion.correctAnswer -> AppColors.Success
                                        isThisSelected -> AppColors.Error
                                        else -> Color.Transparent
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Validate Button — glassmorphism
            if (!isAnswered) {
                GlassButton(
                    onClick = { isAnswered = true },
                    enabled = selectedAnswer != null,
                    accentColor = AppColors.Info
                ) {
                    Text(
                        "Valider",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            // Final Result — glassmorphism
            GlassCard(accentColor = AppColors.Success) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Quiz terminé !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score : $correctAnswers/$totalQuestions",
                        fontSize = 18.sp,
                        color = AppColors.OnSurface
                    )
                }
            }
        }

        if (!showResult) {
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onBack) {
                Text("← Changer d'activité", color = AppColors.OnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}

// ========== CALCULS MATHÉMATIQUES ==========

@Composable
fun MathChallenge(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val totalProblems = AppPreferences.getMathCount(context)

    var currentProblemIndex by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var currentProblem by remember { mutableStateOf(generateMathProblem()) }
    var userAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(isAnswered) {
        if (isAnswered) {
            if (isCorrect) correctAnswers++

            delay(2000)
            if (currentProblemIndex < totalProblems - 1) {
                currentProblemIndex++
                currentProblem = generateMathProblem()
                userAnswer = TextFieldValue("")
                isAnswered = false
                isCorrect = false
            } else {
                showResult = true
                delay(2000)
                onComplete()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AppColors.Success,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Calcul Mental",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = "Problème ${currentProblemIndex + 1}/$totalProblems",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }

        if (!showResult) {
            // Math Expression — glassmorphism
            GlassCard(accentColor = AppColors.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${currentProblem.num1} ${currentProblem.operator} ${currentProblem.num2} = ?",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Answer Input
            if (!isAnswered) {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Ta réponse") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.SurfaceVariant,
                        focusedLabelColor = AppColors.Primary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Validate Button — glassmorphism
                GlassButton(
                    onClick = {
                        isAnswered = true
                        isCorrect = userAnswer.text.trim() == currentProblem.answer.toString()
                    },
                    enabled = userAnswer.text.isNotEmpty(),
                    accentColor = AppColors.Success
                ) {
                    Text(
                        "Valider",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            } else {
                // Result — glassmorphism
                GlassCard(accentColor = if (isCorrect) AppColors.Success else AppColors.Error) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                            contentDescription = null,
                            tint = if (isCorrect) AppColors.Success else AppColors.Error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isCorrect) "Excellent !" else "Réponse incorrecte",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) AppColors.Success else AppColors.Error
                        )
                        if (!isCorrect) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "La réponse était: ${currentProblem.answer}",
                                fontSize = 16.sp,
                                color = AppColors.Error
                            )
                        }
                    }
                }
            }
        } else {
            // Final Result — glassmorphism
            GlassCard(accentColor = AppColors.Success) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Défi terminé !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score : $correctAnswers/$totalProblems",
                        fontSize = 18.sp,
                        color = AppColors.OnSurface
                    )
                }
            }
        }

        if (!showResult) {
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onBack) {
                Text("← Changer d'activité", color = AppColors.OnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}

data class MathProblem(val num1: Int, val num2: Int, val operator: String, val answer: Int)

fun generateMathProblem(): MathProblem {
    val operations = listOf("+", "-", "×")
    val op = operations.random()

    return when (op) {
        "×" -> {
            val num1 = Random.nextInt(3, 15)
            val num2 = Random.nextInt(3, 15)
            MathProblem(num1, num2, op, num1 * num2)
        }
        "+" -> {
            val num1 = Random.nextInt(20, 100)
            val num2 = Random.nextInt(20, 100)
            MathProblem(num1, num2, op, num1 + num2)
        }
        else -> { // "-"
            val num2 = Random.nextInt(20, 50)
            val answer = Random.nextInt(10, 80)
            val num1 = answer + num2
            MathProblem(num1, num2, op, answer)
        }
    }
}

// ========== PUZZLES LOGIQUES ==========
// (Continuera dans le prochain message à cause de la limite de caractères)

data class LogicPuzzle(
    val question: String,
    val answer: String,
    val hint: String
)

val logicPuzzles = listOf(
    LogicPuzzle(
        "Quel nombre complète la suite : 2, 4, 8, 16, ?",
        "32",
        "Chaque nombre est multiplié par 2"
    ),
    LogicPuzzle(
        "Un fermier a 17 moutons. Tous sauf 9 meurent. Combien en reste-t-il ?",
        "9",
        "Lis attentivement : 'tous sauf 9'"
    ),
    LogicPuzzle(
        "Quel est le nombre suivant : 1, 1, 2, 3, 5, 8, ?",
        "13",
        "Suite de Fibonacci : additionne les deux précédents"
    ),
    LogicPuzzle(
        "Si 5 chats attrapent 5 souris en 5 minutes, combien de chats faut-il pour attraper 100 souris en 100 minutes ?",
        "5",
        "Le rapport reste le même"
    ),
    LogicPuzzle(
        "Quel nombre manque : 10, 20, ?, 40, 50",
        "30",
        "Série de 10 en 10"
    )
)

@Composable
fun PuzzleChallenge(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val totalPuzzles = AppPreferences.getPuzzleCount(context)
    val puzzles = remember { logicPuzzles.shuffled().take(totalPuzzles) }

    var currentPuzzleIndex by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var showHint by remember { mutableStateOf(false) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    val currentPuzzle = puzzles[currentPuzzleIndex]

    LaunchedEffect(isAnswered) {
        if (isAnswered) {
            if (isCorrect) correctAnswers++

            delay(2000)
            if (currentPuzzleIndex < puzzles.size - 1) {
                currentPuzzleIndex++
                userAnswer = TextFieldValue("")
                showHint = false
                isAnswered = false
                isCorrect = false
            } else {
                showResult = true
                delay(2000)
                onComplete()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AppColors.Warning,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Puzzle Logique",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = "Puzzle ${currentPuzzleIndex + 1}/$totalPuzzles",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }

        if (!showResult) {
            // Puzzle Question — glassmorphism
            GlassCard(accentColor = AppColors.Warning) {
                Text(
                    text = currentPuzzle.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hint Button
            if (!showHint && !isAnswered) {
                OutlinedButton(
                    onClick = { showHint = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.Info
                    )
                ) {
                    Icon(Icons.Filled.Star, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Voir l'indice")
                }
            }

            // Hint Card — glassmorphism
            if (showHint) {
                GlassCard(accentColor = AppColors.Info) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = AppColors.Info,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = currentPuzzle.hint,
                            fontSize = 14.sp,
                            color = AppColors.Info,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Answer Input
            if (!isAnswered) {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Ta réponse") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.SurfaceVariant,
                        focusedLabelColor = AppColors.Primary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Validate Button — glassmorphism
                GlassButton(
                    onClick = {
                        isAnswered = true
                        isCorrect = userAnswer.text.trim().equals(currentPuzzle.answer, ignoreCase = true)
                    },
                    enabled = userAnswer.text.isNotEmpty(),
                    accentColor = AppColors.Warning
                ) {
                    Text(
                        "Valider",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            } else {
                // Result — glassmorphism
                GlassCard(accentColor = if (isCorrect) AppColors.Success else AppColors.Error) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                            contentDescription = null,
                            tint = if (isCorrect) AppColors.Success else AppColors.Error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isCorrect) "Bravo !" else "Pas tout à fait...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) AppColors.Success else AppColors.Error
                        )
                        if (!isCorrect) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "La réponse était: ${currentPuzzle.answer}",
                                fontSize = 16.sp,
                                color = AppColors.Error
                            )
                        }
                    }
                }
            }
        } else {
            // Final Result — glassmorphism
            GlassCard(accentColor = AppColors.Success) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Puzzles terminés !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score : $correctAnswers/$totalPuzzles",
                        fontSize = 18.sp,
                        color = AppColors.OnSurface
                    )
                }
            }
        }

        if (!showResult) {
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onBack) {
                Text("← Changer d'activité", color = AppColors.OnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}

// ========== MÉDITATION GUIDÉE ==========

val meditationPhrases = listOf(
    "Installez-vous confortablement...",
    "Fermez les yeux doucement...",
    "Respirez profondément par le nez...",
    "Retenez votre respiration 3 secondes...",
    "Expirez lentement par la bouche...",
    "Sentez votre corps se détendre...",
    "Relâchez les tensions dans vos épaules...",
    "Concentrez-vous sur le moment présent...",
    "Laissez passer vos pensées sans les juger...",
    "Vous êtes calme et centré..."
)

@Composable
fun MeditationChallenge(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val totalDuration = AppPreferences.getMeditationDuration(context)
    val phraseDuration = (totalDuration / meditationPhrases.size).coerceAtLeast(3)

    var currentPhase by remember { mutableStateOf(0) }
    var phraseTimer by remember { mutableStateOf(phraseDuration) }

    LaunchedEffect(Unit) {
        while (currentPhase < meditationPhrases.size) {
            while (phraseTimer > 0) {
                delay(1000)
                phraseTimer--
            }
            currentPhase++
            phraseTimer = phraseDuration
        }
        onComplete()
    }

    val progress = currentPhase.toFloat() / meditationPhrases.size.toFloat()
    val isComplete = currentPhase >= meditationPhrases.size

    // Fonctionnelle : animation de respiration pour le cercle
    val breatheScale by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Glow subtil en verre pour le cercle de respiration (50% de l'opacité originale)
        Box(
            modifier = Modifier
                .size(350.dp)
                .scale(breatheScale)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AppColors.Primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Méditation Guidée",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Current phrase — glassmorphism
            if (currentPhase < meditationPhrases.size) {
                GlassCard(accentColor = AppColors.Primary) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = meditationPhrases[currentPhase],
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Progress indicator
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(150.dp),
                    color = AppColors.Primary,
                    strokeWidth = 8.dp,
                    trackColor = AppColors.SurfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currentPhase + 1}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    Text(
                        text = "/ ${meditationPhrases.size}",
                        fontSize = 16.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Breathing circle animation — glass border style
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(breatheScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.2f),
                                AppColors.Info.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppColors.GlassHighlightEdge,
                                AppColors.Primary.copy(alpha = 0.2f),
                                AppColors.GlassBorderLight
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (!isComplete) {
                TextButton(onClick = onBack) {
                    Text("← Changer d'activité", color = AppColors.OnSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}
