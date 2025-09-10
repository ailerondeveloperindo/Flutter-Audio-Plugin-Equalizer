import 'dart:convert';

class AlbumArtworkModel {
  String? albumArtworkBase64;
  String? artworkDataType;

  AlbumArtworkModel({
    this.albumArtworkBase64,
    this.artworkDataType,
  });

  factory AlbumArtworkModel.fromJson(Map<String, dynamic> json) {
    return AlbumArtworkModel(
      albumArtworkBase64: json['albumArtworkBase64'] as String?,
      artworkDataType: json['artworkDataType'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'albumArtworkBase64': albumArtworkBase64,
      'artworkDataType': artworkDataType,
    };
  }
}

class SongMetadataModel {
  String? title;
  String? artist;
  String? album;
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
  int durationMs;
  String? size;
  String? author;
  String? composer;
  int? userRating;
  int? overallRating;
  String? conductor;
  String? albumArtworkBase64;
  int? artworkDataType;
  String? artworkUri;
  int? trackNumber;
  int? totalTrackCount;

  SongMetadataModel({
    this.title,
    this.artist,
    this.album,
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
    this.durationMs = 0,
    this.size,
    this.author,
    this.composer,
    this.userRating,
    this.overallRating,
    this.conductor,
    this.albumArtworkBase64,
    this.artworkDataType,
    this.artworkUri,
    this.trackNumber,
    this.totalTrackCount,
  });

  factory SongMetadataModel.fromJson(Map<String, dynamic> json) {
    return SongMetadataModel(
      title: json['title'] as String?,
      artist: json['artist'] as String?,
      album: json['album'] as String?,
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
      durationMs: json['songDurationMs'] as int? ?? 0,
      size: json['size'] as String?,
      author: json['author'] as String?,
      composer: json['composer'] as String?,
      userRating: json['userRating'] as int?,
      overallRating: json['overallRating'] as int?,
      conductor: json['conductor'] as String?,
      albumArtworkBase64: json['albumArtworkBase64'] as String?,
      artworkDataType: json['artworkDataType'] as int?,
      artworkUri: json['artworkUri'] as String?,
      trackNumber: json['trackNumber'] as int?,
      totalTrackCount: json['totalTrackCount'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'artist': artist,
      'album': album,
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
      'durationMs': durationMs,
      'size': size,
      'author': author,
      'composer': composer,
      'userRating': userRating,
      'overallRating': overallRating,
      'conductor': conductor,
      'albumArtworkBase64': albumArtworkBase64,
      'artworkDataType': artworkDataType,
      'artworkUri': artworkUri,
      'trackNumber': trackNumber,
      'totalTrackCount': totalTrackCount,
    };
  }
}