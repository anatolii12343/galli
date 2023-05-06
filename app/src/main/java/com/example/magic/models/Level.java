package com.example.magic.models;

public enum Level {

    // Получаем список продуктов
    MUM(""),

    // Идем за продуктами
    GO_TO_SHOPPING(""),

    // Собираем
    SHOPPING("Закупка"),

    // Диалог со стариком
    OLD_MAN(""),

    // Выкладывать продукты
    PUT_PRODUCTS(""),

    // Таблетки для старика
    PILLS(""),

    // Баба Яга, идем к Бабе Яге
    BABA_YAGA("Баба Яга"),

    // Идем за Снупом
    SNUP("???"),

    // Отдать Снуп Бабе Яге
    GIVE_SNUP(""),

    // Идем к Кощею
    KOSHEY("Кощей"),

    // Ищем яйцо
    EGG("Яйцо Кощея"),

    // Возвращаем яйцо
    RETURN_EGG(""),

    // Лечим маму
    HEALTH_MUM("Лечись"),

    // Последний уровень
    LAST("");

    public String name;

    Level(String name) {
        this.name = name;
    }
}