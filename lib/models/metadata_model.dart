import 'dart:convert';

class AlbumArtworkModel {
  String? songAlbumCoverBase64;
  String? artworkDataType;

  AlbumArtworkModel({
    this.songAlbumCoverBase64,
    this.artworkDataType,
  });

  factory AlbumArtworkModel.fromJson(Map<String, dynamic> json) {
    return AlbumArtworkModel(
      songAlbumCoverBase64: json['songAlbumCoverBase64'] as String?,
      artworkDataType: json['artworkDataType'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'songAlbumCoverBase64': songAlbumCoverBase64,
      'artworkDataType': artworkDataType,
    };
  }
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

  factory SongMetadataModel.fromJson(Map<String, dynamic> json) {
    return SongMetadataModel(
      songTitle: json['songTitle'] as String?,
      songArtist: json['songArtist'] as String?,
      songAlbum: json['songAlbum'] as String?,
      recordingYear: json['recordingYear'] as int?,
      recordingMonth: json['recordingMonth'] as int?,
      recordingDay: json['recordingDay'] as int?,
      releaseYear: json['releaseYear'] as int?,
      releaseMonth: json['releaseMonth'] as int?,
      releaseDay: json['releaseDay'] as int?,
      songWriter: json['songWriter'] as String?,
      discNumber: json['discNumber'] as int?,
      totalDiscCount: json['totalDiscCount'] as int?,
      genre: json['genre'] as String?,
      compilation: json['compilation'] as String?,
      station: json['station'] as String?,
      songDurationMs: json['songDurationMs'] as int? ?? 0,
      songSize: json['songSize'] as String?,
      songAuthor: json['songAuthor'] as String?,
      songComposer: json['songComposer'] as String?,
      userRating: json['userRating'] as int?,
      overallRating: json['overallRating'] as int?,
      conductor: json['conductor'] as String?,
      songAlbumCoverBase64: json['songAlbumCoverBase64'] as String?,
      artworkDataType: json['artworkDataType'] as int?,
      artworkUri: json['artworkUri'] as String?,
      trackNumber: json['trackNumber'] as int?,
      totalTrackCount: json['totalTrackCount'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'songTitle': songTitle,
      'songArtist': songArtist,
      'songAlbum': songAlbum,
      'recordingYear': recordingYear,
      'recordingMonth': recordingMonth,
      'recordingDay': recordingDay,
      'releaseYear': releaseYear,
      'releaseMonth': releaseMonth,
      'releaseDay': releaseDay,
      'songWriter': songWriter,
      'discNumber': discNumber,
      'totalDiscCount': totalDiscCount,
      'genre': genre,
      'compilation': compilation,
      'station': station,
      'songDurationMs': songDurationMs,
      'songSize': songSize,
      'songAuthor': songAuthor,
      'songComposer': songComposer,
      'userRating': userRating,
      'overallRating': overallRating,
      'conductor': conductor,
      'songAlbumCoverBase64': songAlbumCoverBase64,
      'artworkDataType': artworkDataType,
      'artworkUri': artworkUri,
      'trackNumber': trackNumber,
      'totalTrackCount': totalTrackCount,
    };
  }
}