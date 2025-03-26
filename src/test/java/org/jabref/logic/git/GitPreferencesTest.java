package org.jabref.logic.git;

import javafx.beans.property.BooleanProperty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GitPreferencesTest {

    private GitPreferences gitPreferences;

    @BeforeEach
    void setUp() {
        gitPreferences = new GitPreferences(true, "", "");
    }

    @Test
    void constructorInitialisesValues() {
        assertThat(gitPreferences.getAutoPushEnabled()).isTrue();
    }

    @Test
    void gettersReturnCorrectValues() {
        assertThat(gitPreferences.getAutoPushEnabled()).isTrue();
    }

    @Test
    void setAutoPushEnabledUpdatesValue() {
        gitPreferences.setAutoPushEnabled(false);
        assertThat(gitPreferences.getAutoPushEnabled()).isFalse();
    }

    @Test
    void javaFXBooleanPropertyUpdates() {
        BooleanProperty autoPushProperty = gitPreferences.getAutoPushEnabledProperty();
        autoPushProperty.set(false);
        assertThat(gitPreferences.getAutoPushEnabled()).isFalse();
    }
}
