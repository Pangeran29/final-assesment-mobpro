-- CreateTable
CREATE TABLE "File" (
    "id" SERIAL NOT NULL,
    "upload_by" TEXT,
    "file_location" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX "File_id_key" ON "File"("id");
