class AlbumArtworkModel {
  String? songAlbumCoverBase64;
  String? artworkDataType;

  AlbumArtworkModel({
    this.songAlbumCoverBase64,
    this.artworkDataType,
  });
}

class SongMetadataModel {
  String? songTitle;
  String? songArtist;
  String? songAlbum;
  int songDurationMs;
  String? songSize;
  String? songAuthor;
  String? songAlbumCoverBase64;

  SongMetadataModel({
    this.songTitle,
    this.songArtist,
    this.songAlbum,
    this.songDurationMs = 0,
    this.songSize,
    this.songAuthor,
    this.songAlbumCoverBase64,
  });
}