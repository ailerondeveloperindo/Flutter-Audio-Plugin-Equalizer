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
  int? recordingYear;
  int? recordingMonth;
  int? recordingDay;
  int? releaseYear;
  int? releaseMonth;
  int? releaseDay;
  String? songWriter;
  int? discNumber;
  int? totalDiscCount;
  String? genre;
  String? compilation;
  String? station;
  int songDurationMs;
  String? songSize;
  String? songAuthor;
  String? songComposer;
  int? userRating;
  int? overallRating;
  String? conductor;
  String? songAlbumCoverBase64;
  int? artworkDataType;
  String? artworkUri;
  int? trackNumber;
  int? totalTrackCount;

  SongMetadataModel({
    this.songTitle,
    this.songArtist,
    this.songAlbum,
    this.recordingYear,
    this.recordingMonth,
    this.recordingDay,
    this.releaseYear,
    this.releaseMonth,
    this.releaseDay,
    this.songWriter,
    this.discNumber,
    this.totalDiscCount,
    this.genre,
    this.compilation,
    this.station,
    this.songDurationMs = 0,
    this.songSize,
    this.songAuthor,
    this.songComposer,
    this.userRating,
    this.overallRating,
    this.conductor,
    this.songAlbumCoverBase64,
    this.artworkDataType,
    this.artworkUri,
    this.trackNumber,
    this.totalTrackCount,
  });
}