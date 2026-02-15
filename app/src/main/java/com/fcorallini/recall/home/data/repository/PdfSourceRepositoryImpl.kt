package com.fcorallini.recall.home.data.repository

import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.entity.toDomain
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PdfSourceRepositoryImpl @Inject constructor(
    private val pdfSourceDao: PdfSourceDao
) : PdfSourceRepository {

    override suspend fun insert(pdfSource: PdfSource) {
        pdfSourceDao.insert(pdfSource.toEntity())
    }

    override fun observeAll(): Flow<List<PdfSource>> {
        return pdfSourceDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: String): PdfSource? {
        return pdfSourceDao.getById(id)?.toDomain()
    }
}
