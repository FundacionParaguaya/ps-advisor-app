package org.fundacionparaguaya.advisorapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.fundacionparaguaya.advisorapp.models.Family;

/**
 * The database storing a local cache of data for the user.
 */
@Database(entities = {Family.class}, version = 1)
public abstract class AdvisorDatabase extends RoomDatabase {

    public abstract FamilyDao familyDao();
}