package com.fcorallini.recall.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fcorallini.recall.core.data.db.converter.Converters
import com.fcorallini.recall.core.data.db.dao.PdfSourceDao
import com.fcorallini.recall.core.data.db.dao.PracticeSessionDao
import com.fcorallini.recall.core.data.db.dao.QuestionDao
import com.fcorallini.recall.core.data.db.entity.PdfSourceEntity
import com.fcorallini.recall.core.data.db.entity.PracticeSessionEntity
import com.fcorallini.recall.core.data.db.entity.QuestionEntity

@Database(
    entities = [PdfSourceEntity::class, QuestionEntity::class, PracticeSessionEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun pdfSourceDao(): PdfSourceDao
    abstract fun questionDao(): QuestionDao
    abstract fun practiceSessionDao(): PracticeSessionDao
}
