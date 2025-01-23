package com.example.javalab78;

import android.graphics.Color;
import android.os.Bundle; // Импорт класса для работы с состоянием активности
import android.os.Handler; // задержк
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity; // Импорт базового класса активности с поддержкой современных функций
import androidx.core.content.ContextCompat; // доступа к ресурсам
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Button[][] buttons = new Button[3][3];
    private TextView statusText, scoreText;
    private int crossWins = 0, noughtWins = 0;
    private String currentPlayer = "X";
    private int moveCount = 0;
    private int defaultButtonColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // СОздание активности
        super.onCreate(savedInstanceState); // Вызов базового метода onCreate
        setContentView(R.layout.activity_main); // Установка макета активности(r сс на все рес-ы)

        statusText = findViewById(R.id.statusText); // Инициализация текстового поля ст-а игры
        scoreText = findViewById(R.id.scoreText); // поля счёта

        defaultButtonColor = ContextCompat.getColor(this, R.color.purple_500); // Получение цвета кнопок по умолчанию из ресурсов

        initializeButtons(); // Инициализация кнопок игрового поля

        Button resetButton = findViewById(R.id.resetButton); // Поиск кнопки сброса по ID
        resetButton.setOnClickListener(v -> resetGame()); // Установка обработчика нажатия для кнопки сброса

        assignFirstPlayer(); //выб-м 1-го игр
    }


///Инициа-м кнопки
    private void initializeButtons() {
        for (int row = 0; row < 3; row++) { //стрки
            for (int col = 0; col < 3; col++) {
                int resID = getResources().getIdentifier("button_" + row + col, "id", getPackageName()); // Получение идентификатора кнопки
                buttons[row][col] = findViewById(resID); // Привязка кнопки к массиву
                buttons[row][col].setOnClickListener(this::onCellClick); // Установка обработчика нажатия для кнопки

                buttons[row][col].setText(""); // ничего не пишем в кнпоке
                buttons[row][col].setBackgroundColor(defaultButtonColor); // Установл-м цвет кнопки по умолчанию
            }
        }
    }

    ///Нажатие на копку (уст Х / 0)
    private void onCellClick(View v) {
        Button button = (Button) v; // Приведение нажатого элемента к кнопке
        if (button.getText().toString().isEmpty() && !isGameOver()) { // Проверка, что кнопка пуста и игра не окончена
            button.setText(currentPlayer); // Установка текста текущего игрока на кнопку
            button.setEnabled(false); // Отключение кнопки после хода
            button.setBackgroundColor(Color.GRAY); //деаемм серой
            moveCount++;

            if (checkWinner()) { // Проверка на победителя
                highlightWinner(); // Подсветка победителя
                updateScore();
                return;
            }

            if (moveCount == 9) {
                statusText.setText("Ничья!");
                new Handler().postDelayed(this::resetBoardWithoutScoreReset, 2000);
                return;
            }

            switchPlayer();
        }
    }

    ///Пров-м подедителя
    private boolean checkWinner() {
        for (int i = 0; i < 3; i++) { // Цикл по строкам и столбцам
            if (isLineMatch(buttons[i][0], buttons[i][1], buttons[i][2]) || // Проверка строки
                    isLineMatch(buttons[0][i], buttons[1][i], buttons[2][i])) { // Проверка столбца
                return true; // Возврат значения "победа"
            }
        }
        return isLineMatch(buttons[0][0], buttons[1][1], buttons[2][2]) || // Проверка главной диагонали
                isLineMatch(buttons[0][2], buttons[1][1], buttons[2][0]); // Проверка побочной диагонали
    }

    ///совп-е линии
    private boolean isLineMatch(Button b1, Button b2, Button b3) {
        if (b1.getText().equals(currentPlayer) &&
                b2.getText().equals(currentPlayer) &&
                b3.getText().equals(currentPlayer)) { // Проверка совпадения текста на трёх кнопках
            b1.setBackgroundColor(Color.GREEN);
            b2.setBackgroundColor(Color.GREEN);
            b3.setBackgroundColor(Color.GREEN);
            return true;
        }
        return false;
    }

    ///Выводим поб-ля
    private void highlightWinner() {
        statusText.setText(getString(R.string.winner, currentPlayer.toLowerCase())); // Обновление текста статуса игры
        new Handler().postDelayed(this::resetBoardWithoutScoreReset, 4000);
    }

    ///Перпеключаем тек-го игрока
    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X"; // Переключение между крестиками и ноликами
        statusText.setText(getString(R.string.turn, currentPlayer.toLowerCase())); // Обновление текста статуса игры
    }

    ///превый случайный игрок
    private void assignFirstPlayer() {
        Random random = new Random();
        currentPlayer = random.nextBoolean() ? "X" : "O";
        statusText.setText(getString(R.string.turn, currentPlayer.toLowerCase())); // Обновление текста статуса игры
    }

    ///обновляем счет
    private void updateScore() {
        if (currentPlayer.equals("X")) {
            crossWins++;
        } else {
            noughtWins++;
        }
        scoreText.setText(getString(R.string.score, crossWins, noughtWins)); // Обновление текста счёта
    }


    private void resetGame() {
        crossWins = 0;
        noughtWins = 0;
        resetBoard();
        moveCount = 0;
        assignFirstPlayer();
        scoreText.setText(getString(R.string.score, crossWins, noughtWins));
    }


    private void resetBoardWithoutScoreReset() {
        for (int row = 0; row < 3; row++) { // Цикл по строкам
            for (int col = 0; col < 3; col++) { //столбцам
                Button button = buttons[row][col]; // Получение текущей кнопки
                button.setText("");
                button.setEnabled(true);
                button.setBackgroundColor(defaultButtonColor);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(this::onCellClick); // Назначение обработчика нажатия
            }
        }
        moveCount = 0;
        assignFirstPlayer();
    }

    private void resetBoard() {
        for (int row = 0; row < 3; row++) { // Цикл по строкам
            for (int col = 0; col < 3; col++) {
                Button button = buttons[row][col];
                button.setText("");
                button.setEnabled(true);
                button.setBackgroundColor(defaultButtonColor);
            }
        }
        moveCount = 0;
    }

    ///Проверяем окончание игры
    private boolean isGameOver() {
        return moveCount == 9 || checkWinner();
    }
}
