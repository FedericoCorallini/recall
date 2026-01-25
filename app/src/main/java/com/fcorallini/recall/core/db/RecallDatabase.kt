package com.fcorallini.recall.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fcorallini.recall.core.db.converter.Converters
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.dao.QuestionDao
import com.fcorallini.recall.core.db.entity.PdfSourceEntity
import com.fcorallini.recall.core.db.entity.QuestionEntity

@Database(
    entities = [PdfSourceEntity::class, QuestionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun pdfSourceDao(): PdfSourceDao
    abstract fun questionDao(): QuestionDao
}
